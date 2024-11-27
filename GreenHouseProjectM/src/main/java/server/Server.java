package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.Locale;

public class Server {
  private static final int PORT = 12345;
  private final ExecutorService threadPool = Executors.newCachedThreadPool();
  private final Map<Integer, SensorState> sensorStates = new ConcurrentHashMap<>();
  private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

  public static void main(String[] args) {
    new Server().start();
  }

  public void start() {
    // Initialize some sample sensor states
    sensorStates.put(1, new SensorState(1, 25.0, 50.0));
    sensorStates.put(2, new SensorState(2, 22.0, 60.0));
    sensorStates.put(3, new SensorState(3, 28.0, 40.0));

    // Start periodic updates
    startSensorUpdates();

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Server is running on port " + PORT);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress());
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        clients.add(clientHandler);
        threadPool.execute(clientHandler);
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }


  private void broadcastUpdate(String message) {
    synchronized (clients) {
      for (ClientHandler client : clients) {
        client.send(message);
      }
    }
  }

  private class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      try (
          BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      ) {
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send initial sensor states to the client
        synchronized (sensorStates) {
          for (SensorState state : sensorStates.values()) {
            out.println(state.toUpdateMessage());
          }
        }

        // Handle incoming commands
        String message;
        while ((message = in.readLine()) != null) {
          System.out.println("Received: " + message);

          // Process command and update sensor state
          if (message.startsWith("COMMAND|")) {
            processCommand(message);
          }
        }
      } catch (IOException e) {
        System.err.println("Client disconnected: " + e.getMessage());
      } finally {
        disconnect();
      }
    }

    private void processCommand(String command) {
      String[] parts = command.split("\\|");
      if (parts.length < 3) return;

      int nodeId = Integer.parseInt(parts[1]);
      String[] actuatorUpdate = parts[2].split("=");
      if (actuatorUpdate.length == 2) {
        String actuator = actuatorUpdate[0];
        boolean state = actuatorUpdate[1].equalsIgnoreCase("on");

        // Update actuator state
        SensorState sensorState = sensorStates.get(nodeId);
        if (sensorState != null) {
          sensorState.setActuatorState(actuator, state);

          // Broadcast updated state to all clients
          String updateMessage = sensorState.toUpdateMessage();
          System.out.println("Broadcasting update: " + updateMessage);
          broadcastUpdate(updateMessage);
        }
      }
    }




    private void send(String message) {
      if (out != null) {
        out.println(message);
      }
    }

    private void disconnect() {
      try {
        clients.remove(this);
        socket.close();
      } catch (IOException e) {
        System.err.println("Error closing client socket: " + e.getMessage());
      }
    }
  }

  // Maintain sensor state globally in the server
  private static class SensorState {
    private final int nodeId;
    private double temperature;
    private double humidity;
    private final Map<String, Boolean> actuators;

    public SensorState(int nodeId, double temperature, double humidity) {
      this.nodeId = nodeId;
      this.temperature = temperature;
      this.humidity = humidity;
      this.actuators = new ConcurrentHashMap<>();
      // Initialize actuators with default states
      actuators.put("heater", false);
      actuators.put("fan", false);
      actuators.put("window", false);
    }
    public synchronized void setActuatorState(String actuator, boolean state) {
      actuators.put(actuator, state);
    }
    public synchronized boolean getActuatorState(String actuator) {
      return actuators.getOrDefault(actuator, false);
    }
    public synchronized void updateRandomly() {
      // Simulate sensor value changes
      temperature += (Math.random() - 0.5); // Change temperature slightly
      humidity += (Math.random() - 0.5);   // Change humidity slightly
      temperature = Math.max(15, Math.min(35, temperature)); // Clamp to realistic range
      humidity = Math.max(20, Math.min(80, humidity));       // Clamp to realistic range
    }

    public synchronized String toUpdateMessage() {
      String actuatorStates = actuators.entrySet()
          .stream()
          .map(entry -> entry.getKey() + "=" + (entry.getValue() ? "on" : "off"))
          .reduce((a, b) -> a + "|" + b)
          .orElse("");
      return String.format(Locale.US, "UPDATE|nodeId=%d|temperature=%.2f|humidity=%.2f|%s",
          nodeId, temperature, humidity, actuatorStates);
    }
  }

  // Periodically update sensor values and notify clients
  private void startSensorUpdates() {
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      synchronized (sensorStates) {
        for (SensorState state : sensorStates.values()) {
          state.updateRandomly(); // Simulate changes
          String updateMessage = state.toUpdateMessage();
          broadcastUpdate(updateMessage); // Send to all clients
        }
      }
    }, 0, 1, TimeUnit.SECONDS);
  }
}

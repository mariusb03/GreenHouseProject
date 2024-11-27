package client;

import java.io.*;
import java.net.*;
import javafx.application.Platform;

public class Client {
  private final String host;
  private final int port;
  private PrintWriter out;
  private ClientListener listener;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
    connect();
  }

  public void setListener(ClientListener listener) {
    this.listener = listener;
  }

  private void connect() {
    try {
      Socket socket = new Socket(host, port);
      out = new PrintWriter(socket.getOutputStream(), true);

      // Start listening for updates
      new Thread(() -> {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
          String message;
          while ((message = in.readLine()) != null) {
            System.out.println("Received: " + message);
            if (listener != null && message.startsWith("UPDATE|")) {
              final String finalMessage = message;
              Platform.runLater(() -> listener.onUpdateReceived(finalMessage));
            }
          }
        } catch (IOException e) {
          System.err.println("Error reading from server: " + e.getMessage());
        }
      }).start();
    } catch (IOException e) {
      System.err.println("Failed to connect to server: " + e.getMessage());
    }
  }

  public void sendCommand(String command) {
    if (out != null) {
      out.println(command);
    }
  }

  public interface ClientListener {
    void onUpdateReceived(String update);
  }
}


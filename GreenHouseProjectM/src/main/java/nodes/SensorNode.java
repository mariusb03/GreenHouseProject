package nodes;

import java.io.*;
import java.net.*;
import java.util.Random;

public class SensorNode {
  private static final int SERVER_PORT = 12345;
  private final String nodeId;

  public SensorNode(String nodeId) {
    this.nodeId = nodeId;
  }

  public void start() {
    try (Socket socket = new Socket("127.0.0.1", SERVER_PORT);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

      Random random = new Random();
      while (true) {
        String sensorData = String.format("SENSOR_DATA|%s|temperature=%.2f°C,humidity=%.2f%%",
            nodeId, 20 + random.nextDouble() * 10, 50 + random.nextDouble() * 20);
        out.println(sensorData);
        System.out.println("Sent: " + sensorData);
        Thread.sleep(5000);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Sensor node error: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    new SensorNode("1").start();
  }
}

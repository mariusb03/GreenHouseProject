package nodes;

import java.io.*;
import java.net.*;

public class ControlPanelNode {
  private static final int SERVER_PORT = 12345;

  public void start() {
    try (Socket socket = new Socket("127.0.0.1", SERVER_PORT);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

      // Example: Sending a command to toggle an actuator
      String command = "COMMAND|1|toggle_heater";
      out.println(command);
      System.out.println("Sent: " + command);

      // Listening for incoming data
      String message;
      while ((message = in.readLine()) != null) {
        System.out.println("Received: " + message);
      }
    } catch (IOException e) {
      System.err.println("Control panel error: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    new ControlPanelNode().start();
  }
}

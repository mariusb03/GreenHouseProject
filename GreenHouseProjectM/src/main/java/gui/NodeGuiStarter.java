package gui;

import client.Client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nodes.SensorActuatorNode;

public class NodeGuiStarter extends Application {

  @Override
  public void start(Stage primaryStage) {
    // Create a sample SensorActuatorNode
    SensorActuatorNode node = new SensorActuatorNode(1);

    // Create a Client instance for communication with the server
    Client client = new Client("127.0.0.1", 12345); // Replace with your server's IP and port

    // Pass the node and client to the NodeGuiWindow
    NodeGuiWindow nodeGuiWindow = new NodeGuiWindow(node, client);

    // Wrap NodeGuiWindow in a Scene and display it
    Scene scene = new Scene(nodeGuiWindow, 300, 400);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Node " + node.getId());
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

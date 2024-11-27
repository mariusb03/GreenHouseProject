package gui;

import client.Client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nodes.SensorActuatorNode;

public class NodeGuiWindowLauncher extends Application {

  @Override
  public void start(Stage primaryStage) {
    // Create a sample SensorActuatorNode (Node ID: 1)
    SensorActuatorNode node = new SensorActuatorNode(1);

    // Create a Client instance for this node
    Client client = new Client("127.0.0.1", 12345); // Server IP and port

    // Create the NodeGuiWindow with the node and client
    NodeGuiWindow nodeGuiWindow = new NodeGuiWindow(node, client);

    // Wrap NodeGuiWindow in a Scene and set it in the Stage
    Scene scene = new Scene(nodeGuiWindow, 400, 400); // Adjust dimensions as needed
    primaryStage.setScene(scene);
    primaryStage.setTitle("Node " + node.getId());
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

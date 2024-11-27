package gui;

import client.Client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import nodes.SensorActuatorNode;

public class ControlPanelWithServer extends Application {

  @Override
  public void start(Stage primaryStage) {
    // Start the server in the background(if needed)
    startServerInBackground();

    // Create a TabPane to hold all node tabs
    TabPane tabPane = new TabPane();

    // Add sample nodes as tabs
    addNodeTab(tabPane, new SensorActuatorNode(1));
    addNodeTab(tabPane, new SensorActuatorNode(2));
    addNodeTab(tabPane, new SensorActuatorNode(3));

    // Create the scene with the TabPane
    Scene scene = new Scene(tabPane, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Control Panel with Server");
    primaryStage.show();

  }

  private void addNodeTab(TabPane tabPane, SensorActuatorNode node) {
    // Create a Client instance for this node
    Client client = new Client("127.0.0.1", 12345); // Replace with your server's IP and port

    // Create the NodeGuiWindow with the node and client
    NodeGuiWindow nodeGui = new NodeGuiWindow(node, client);

    // Create a tab with the node's ID as the title
    Tab tab = new Tab("Node " + node.getId(), nodeGui);
    tabPane.getTabs().add(tab);
  }

  private void startServerInBackground() {
    // Optional: Start a server thread if needed
    new Thread(() -> {
      System.out.println("Server is running...");
      try {
        Thread.sleep(Long.MAX_VALUE); // Simulate a running server
      } catch (InterruptedException e) {
        System.err.println("Server interrupted.");
      }
    }).start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

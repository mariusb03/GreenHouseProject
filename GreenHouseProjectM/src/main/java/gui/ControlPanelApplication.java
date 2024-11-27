package gui;

import client.Client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import nodes.SensorActuatorNode;

public class ControlPanelApplication extends Application {

  @Override
  public void start(Stage primaryStage) {
    // Create a TabPane to hold multiple node tabs
    TabPane tabPane = new TabPane();

    // Add example nodes as tabs
    addNodeTab(tabPane, new SensorActuatorNode(1));
    addNodeTab(tabPane, new SensorActuatorNode(2));
    addNodeTab(tabPane, new SensorActuatorNode(3));

    // Set up the scene with the TabPane
    Scene scene = new Scene(tabPane, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Control Panel Application");
    primaryStage.show();
  }

  private void addNodeTab(TabPane tabPane, SensorActuatorNode node) {
    // Create a Client instance for this node
    Client client = new Client("127.0.0.1", 12345); // Replace with your server's IP and port

    // Create the NodeGuiWindow with the node and client
    NodeGuiWindow nodeGui = new NodeGuiWindow(node, client);

    // Create a new tab for the node
    Tab tab = new Tab("Node " + node.getId(), nodeGui);

    // Add the tab to the TabPane
    tabPane.getTabs().add(tab);
  }

  public static void main(String[] args) {
    launch(args); // Start the JavaFX application
  }
}

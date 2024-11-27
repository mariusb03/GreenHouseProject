package gui;

import client.Client;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import nodes.SensorActuatorNode;
import java.util.HashMap;
import java.util.Map;

public class NodeGuiWindow extends VBox {
  private final SensorActuatorNode node;
  private final Label temperatureLabel;
  private final Label humidityLabel;
  private final Client client;
  private VBox actuatorPane;
  private final Map<String, CheckBox> actuatorToggles = new HashMap<>();


  public NodeGuiWindow(SensorActuatorNode node, Client client) {
    this.node = node;
    this.client = client;

    // Initialize actuatorPane
    actuatorPane = new VBox();
    actuatorPane.setSpacing(10);

    // Display sensors
    temperatureLabel = new Label("temperature: " + node.getFormattedTemperature());
    humidityLabel = new Label("humidity: " + node.getFormattedHumidity());
    VBox sensorBox = new VBox(temperatureLabel, humidityLabel);

    TitledPane sensorsPane = new TitledPane("Sensors", sensorBox);

    // Actuator controls
    VBox actuatorPane = new VBox();
    for (String actuator : node.getActuators().keySet()) {
      CheckBox toggle = new CheckBox(actuator + ": off");
      toggle.setOnAction(event -> {
        boolean isOn = toggle.isSelected();
        node.getActuators().put(actuator, isOn);
        toggle.setText(actuator + ": " + (isOn ? "on" : "off"));

        // Send actuator state to the server
        client.sendCommand("COMMAND|" + node.getId() + "|" + actuator + "=" + (isOn ? "on" : "off"));
      });
      actuatorPane.getChildren().add(toggle);
    }

    TitledPane actuatorsPane = new TitledPane("Actuators", actuatorPane);

    getChildren().addAll(sensorsPane, actuatorsPane);

    // Register for sensor updates
    client.setListener(update -> {
      if (update.contains("nodeId=" + node.getId())) {
        String[] parts = update.split("\\|");
        for (String part : parts) {
          if (part.contains("=")) { // Handle both sensors and actuators
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
              String key = keyValue[0];
              String value = keyValue[1].replace(",", ".");
              if (key.equals("temperature")) {
                node.setTemperature(Double.parseDouble(value));
              } else if (key.equals("humidity")) {
                node.setHumidity(Double.parseDouble(value));
              } else { // Assume it's an actuator
                boolean state = value.equalsIgnoreCase("on");
                node.setActuatorState(key, state);
              }
            }
          }
        }
        refreshDisplay();
      }
    });
  }

  private void refreshDisplay() {
    temperatureLabel.setText("temperature: " + node.getFormattedTemperature());
    humidityLabel.setText("humidity: " + node.getFormattedHumidity());

    // Update actuator toggles
    for (String actuator : node.getActuators().keySet()) {
      boolean state = node.getActuators().get(actuator);
      CheckBox toggle = actuatorToggles.get(actuator);
      if (toggle != null) {
        toggle.setSelected(state);
        toggle.setText(actuator + ": " + (state ? "on" : "off"));
      }
    }
  }
}

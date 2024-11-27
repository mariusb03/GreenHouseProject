package nodes;

import java.util.HashMap;
import java.util.Map;

public class SensorActuatorNode {
  private final int id;
  private double temperature;
  private double humidity;
  private final Map<String, Boolean> actuators;

  public SensorActuatorNode(int id) {
    this.id = id;
    this.temperature = 20 + Math.random() * 10;
    this.humidity = 50 + Math.random() * 30;
    this.actuators = new HashMap<>();

    actuators.put("window", false);
    actuators.put("heater", false);
  }

  public int getId() {
    return id;
  }

  public String getFormattedTemperature() {
    return String.format("%.2f°C", temperature);
  }

  public String getFormattedHumidity() {
    return String.format("%.2f%%", humidity);
  }

  public void updateSensorValues() {
    if (actuators.get("heater")) {
      temperature += 0.1;
    } else {
      temperature -= 0.1;
    }
    if (actuators.get("window")) {
      humidity += 0.2;
    } else {
      humidity -= 0.1;
    }

    // Clamp values to realistic ranges
    temperature = Math.max(15, Math.min(35, temperature));
    humidity = Math.max(20, Math.min(80, humidity));
  }

  public Map<String, Boolean> getActuators() {
    return actuators;
  }

  public void setTemperature(double v) {
    temperature = v;
  }

  public void setHumidity(double v) {
    humidity = v;
  }

  public void setActuatorState(String actuator, boolean state) {
    actuators.put(actuator, state);
  }

  public boolean getActuatorState(String actuator) {
    return actuators.getOrDefault(actuator, false);
  }

  public void toggleActuator(String actuator) {
    actuators.put(actuator, !actuators.getOrDefault(actuator, false));
  }
}

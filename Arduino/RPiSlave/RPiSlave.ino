#include <OneWire.h>
#include <DallasTemperature.h>
#include <Wire.h>

#define ONE_WIRE_BUS D5 // Temp Sensor pin

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensor(&oneWire);

void setup() {
  // Start Temp Sensor and Serial communication
  sensor.begin();
  Serial.begin(115200);
}

void loop() {
  temp();
  delay(200);
}

void temp() {
  sensor.requestTemperatures();
  float t = sensor.getTempCByIndex(0);
  char temp[6];
  dtostrf(t, 4, 2, temp);
  Serial.println(temp);
  //Serial.println(sizeof(120.55));
}



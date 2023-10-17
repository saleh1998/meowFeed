#define BLYNK_TEMPLATE_ID "TMPL6lCsn0ye-"
#define BLYNK_TEMPLATE_NAME "weight food dispenser"
#define BLYNK_AUTH_TOKEN "C2lV2IP_li3-CE9gHoRBjDg0O8W8XIOb"
#define BLYNK_PRINT Serial

#include <WiFi.h>
#include <BlynkSimpleEsp32.h>
#include <HX711_ADC.h>
#include <ESP32Servo.h>

char auth[] = BLYNK_AUTH_TOKEN;
char ssid[] = "Moaad-2.4G";
char pass[] = "11qazxsw2";

const int HX711_dout = 4;
const int HX711_sck = 5;
HX711_ADC LoadCell(HX711_dout, HX711_sck);
const int calVal_eepromAdress = 0;
float weight;
float previousWeight = 0;
float threshold = 1.0; // Adjust this threshold according to your needs
float calibrationValue = 13206.25; // Set your calibration value here

Servo myServo;

BlynkTimer timer;

void setup() {
    Serial.begin(57600);
    delay(10);

    LoadCell.begin();
    LoadCell.setCalFactor(calibrationValue);

    unsigned long stabilizingtime = 2000;
    boolean _tare = true;
    LoadCell.start(stabilizingtime, _tare);

    Blynk.begin(auth, ssid, pass);
    myServo.attach(18);

    timer.setInterval(1000L, myTimerEvent);
}

void loop() {
    Blynk.run();
    timer.run();
    measureWeight();
}

void measureWeight() {
    if (LoadCell.update()) {
        weight = LoadCell.getData();
        if (weight < 0) {
            weight = 0.00;
        }

        // Check if weight has significantly changed before sending to Blynk
        if (abs(weight - previousWeight) > threshold) {
            Blynk.virtualWrite(V6, weight);
            Blynk.virtualWrite(V5, weight);
            previousWeight = weight;
        }
    }

    if (Serial.available() > 0) {
        char inByte = Serial.read();
        if (inByte == 't') LoadCell.tareNoDelay();
    }
}

void myTimerEvent() {
    Blynk.virtualWrite(V2, millis() / 1000);
}

BLYNK_WRITE(V0) {
    int pinValue = param.asInt();
    Blynk.virtualWrite(V1, pinValue);
}

BLYNK_WRITE(V4) {
    int buttonState = param.asInt();
    if (buttonState == HIGH) {
        myServo.write(60); // Rotate in one direction
    } else {
        myServo.write(90);  // Neutral (stop) position
    }
}

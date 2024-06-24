#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

#define BT_RXD 2
#define BT_TXD 3

Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();

SoftwareSerial btSerial(BT_RXD, BT_TXD);

int SERVOMIN[12]  = {148, 148, 148, 148, 148, 148, 148, 148, 148, 148, 148, 148};
int SERVOMAX[12]  = {358, 357, 359, 358, 342, 338, 300, 300, 300, 300, 300, 300};
uint8_t initTime = 0;
void setup() {
  Serial.begin(9600);
  btSerial.begin(9600);

  pwm.begin();
  pwm.setPWMFreq(50);
  delay(10);

  for (uint16_t n = 0; n < 12; n++) {
    pwm.setPWM(n, 0, SERVOMAX[n]);
  }
  delay(5000);
}

String btString = "";
void loop() {
  while (btSerial.available()) {
    initTime = millis();
    char btChar = btSerial.read();
    btString += btChar;
    delay(5);
  }
  if (!btString.equals("")) {
    Serial.println("input value: " + btString);
    int btLength = btString.length();
    int servoList[btLength - 1] = {};
    Serial.println(btLength);
    for (int n = 0; n < btLength; n++) {
      if (btString[n] >= 'A') {
        servoList[n] = btString[n] - 'A' + 9;
      } else {
        servoList[n] = btString[n] - '0';
      }
      Serial.println(servoList[n]);
    }

    for (int n = 0; n < btLength; n++) {
      Serial.print("UP: ");
      Serial.println(servoList[n]);
      servoDown(servoList[n]);
    }
    delay(2000);
    for (int n = 0; n < btLength; n++) {
      Serial.print("DOWN: ");
      Serial.println(servoList[n]);
      servoUp(servoList[n]);
    }

    btString = "";
  }
}

void setServoPulse(uint8_t n, double pulse) {
  double pulselength;

  pulselength = 1000000;   // 1,000,000 us per second
  pulselength /= 50;   // 50 Hz

  Serial.print(pulselength); Serial.println(" us per period");
  pulselength /= 4096;  // 12 bits of resolution
  Serial.print(pulselength); Serial.println(" us per bit");
  pulse *= 1000000;  // convert to us
  pulse /= pulselength;
  Serial.println(pulse);
  pwm.setPWM(n, 0, pulse);
}

void servoUp(uint16_t num) {
  pwm.setPWM(num, 0, SERVOMAX[num]);
}

void servoDown(uint16_t num) {
  pwm.setPWM(num, 0, SERVOMIN[num]);
}

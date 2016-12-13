#define soundSpeed 58.2

#define trigPin 2 // Triggers the sensor to send data back to the arduino
#define echoPin 3 // Receive channel for sensor data
#define buttonPin 4 //  Button pin for starting the arduino
#define motorLeftA A0 // Left side positive motor control pin
#define motorLeftB A1 // Left side negative motor control pin
#define motorRightA A2 // Right side positive motor control pin
#define motorRightB A3 // Right side negative motor control pin
#define stateGreen 10 // for moving forward
#define stateYellow 11 // LED for moving Left or right
#define stateRed 12 // LED for stopping
#define LEDPin 13 // LED for function state

#define maximumRange 200 // highest acceptible distance from the ultrasonic sensor
#define minimumRange 0 // lowest acceptible distance from the ultrasonic sensor
#define stepDelay 50 // millisecond pause between each step performed
#define zeroAdjust 2 // Maximum amount of variation from zero distance allowed
#define zeroConfidence 10 // # of measurements made when zeroing distance from wall
#define maxDelta 1 // largest amount of change between measurements
#define minDelta 0.25 // lowest amount of change between measurements
#define serialSpeed 9600 // Arduino communication speed

int minThreshold;
int maxThreshold;
float lastDistance;
boolean state = true;

void setup() { // Begin serial transmission, initialize all pins, set zero distance, wait for start button
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(LEDPin, OUTPUT);
  pinMode(buttonPin, INPUT);
  pinMode(motorLeftA, OUTPUT);
  pinMode(motorLeftB, OUTPUT);
  pinMode(motorRightA, OUTPUT);
  pinMode(motorRightB, OUTPUT);
  pinMode(stateGreen, OUTPUT);
  pinMode(stateYellow, OUTPUT);
  pinMode(stateRed, OUTPUT);
  Serial.begin(serialSpeed);
  startBot();
}

void startBot() {
  digitalWrite(LEDPin, HIGH);
  requireButtonPress();
  digitalWrite(LEDPin, LOW);
  zeroDistance(zeroConfidence);
  digitalWrite(LEDPin, HIGH);
  requireButtonPress();
  digitalWrite(LEDPin, LOW);
}

boolean isButtonDown() {
  return digitalRead(buttonPin) == HIGH;
}

void requireButtonPress() {
  while (isButtonUp()) { // wait for button down

  }
  while (isButtonDown()) { // wait for button up

  }
}

boolean isButtonUp() {
  return digitalRead(buttonPin) == LOW;
}

boolean wasLastDown = false;

void loop() { // measure distance, output its state within the min and max measurements, move accordingly
  if (!wasLastDown && isButtonDown()) {
    stepHalt();

    while (isButtonDown()) {
    }
    startBot();
  }


  wasLastDown = isButtonDown();

  float distance = ping();
  float deltaDistance = distance - lastDistance;

  // If the distance is within reading range
  if (distance >= minimumRange && distance <= maximumRange) {

    int tooMuchDelta = 0;
    if (deltaDistance >= maxDelta) { // Moving away from the wall too quickly
      tooMuchDelta = 1;
    }
    else if (deltaDistance <= -maxDelta) { // moving towards the wall too quickly
      tooMuchDelta = -1;
    }

    int tooLittleDelta = 0;
    if (deltaDistance <= minDelta) { // Moving away from the wall too slowly
      tooLittleDelta = 1;
    }
    else if (deltaDistance >= -minDelta) { // moving towards the wall too slowly
      tooLittleDelta = -1;
    }

    // If within the threshold, move forward
    if ((distance > minThreshold && distance < maxThreshold)) {
      //Serial.println("Forwards");
      stepForward();
    }
    else if (distance > maxThreshold) { // Moving away from wall
      if (tooMuchDelta < 0) { // If turning too sharp, turn back
        //Serial.println("Turning Towards Wall TOO FAST, moving back");
        stepLeft();
      }
      else if (tooLittleDelta < 0) { // Turn towards the wall
        //Serial.println("Turning Towards Wall");
        stepRight();
      }
      else {
        stepForward();
      }
    }
    else if (distance < minThreshold) { // Moving towards wall
      if (tooMuchDelta > 0) { // If turning too sharp, turn back
        //Serial.println("Turning Away from Wall TOO FAST, moving back");
        stepRight();
      }
      else if (tooLittleDelta > 0) { // Turn away from the wall
        //Serial.println("Turning Away from Wall");
        stepLeft();
      }
      else {
        stepForward();
      }
    }

    /*if (distance - lastDistance < maxDelta && distance - lastDistance > -maxDelta) { // check if change between measurements is within maximum change

      } else {

      }

      float deltaDistance = distance - lastDistance;

      Serial.print("Delta: ");
      Serial.println(deltaDistance);

      if (lastDistance > minThreshold && lastDistance < maxThreshold) { // within the thresholds
      stepForward();
      Serial.println("Forward");
      } else if (lastDistance > maxThreshold) { // beyond threshold
      if (deltaDistance < 0) { // If moving towards the wall
        if (deltaDistance > -maxDeltaDistance) {
          stepForward();
          Serial.println("Forward (Right to wall)");
        }
        else {
          stepRight();
          Serial.println("Right (Into delta distance)");
        }
      } else {
        stepRight();
        Serial.println("Right");
      }
      } else if (lastDistance < minThreshold) { // underneath threshold
      if (deltaDistance > 0) { // If moving away from the wall
        if (deltaDistance < maxDeltaDistance) {
          stepForward();
          Serial.println("Forward (Left from wall)");
        }
        else {
          stepLeft();
          Serial.println("Left (Into delta distance)");
        }
        stepForward();
        Serial.println("Forward (Left from wall)");
      } else {
        stepLeft();
        Serial.println("Left");
      }
      } else {
      stepHalt();
      Serial.println("Stop");
      }

      lastDistance = distance;
      Serial.print("Inst: ");
      Serial.print(lastDistance);
      Serial.println();*/

    lastDistance = distance;
  } else {
    Serial.println("Out of range.");
    stepForward();
  }

  delay(stepDelay);
}

void zeroDistance(int measureCount) { // measure a provided amount of times, set the thresholds according to the average
  float sum = 0;
  for (int i = 0; i < measureCount; i++) {
    sum += ping();
  }
  sum /= measureCount;
  lastDistance = sum; // By default, the last distance is the current distanceM
  minThreshold = sum - zeroAdjust;
  maxThreshold = sum + zeroAdjust;
  Serial.print("Zeroed to: ");
  Serial.println(sum);
  digitalWrite(LEDPin, LOW);
  digitalWrite(stateYellow, HIGH);
  digitalWrite(stateGreen, HIGH);
}

float ping() { // receive a reading from the ultrasonic distance sensor
  if (state) {
    digitalWrite(LEDPin, HIGH);
  } else {
    digitalWrite(LEDPin, LOW);
  }

  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);

  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);

  digitalWrite(trigPin, LOW);
  float duration = pulseIn(echoPin, HIGH);

  state = !state;

  return duration / soundSpeed;
}

void stepForward() { // both motors forward
  digitalWrite(motorLeftA, HIGH);
  digitalWrite(motorLeftB, LOW);
  digitalWrite(motorRightA, HIGH);
  digitalWrite(motorRightB, LOW);

  digitalWrite(stateGreen, HIGH);
  digitalWrite(stateYellow, LOW);
  digitalWrite(stateRed, LOW);
}

void stepLeft() { // left motor off, right motor on
  //digitalWrite(motorLeftA, LOW);
  digitalWrite(motorLeftB, LOW);
  digitalWrite(motorRightA, HIGH);
  digitalWrite(motorRightB, LOW);

  tone(motorLeftA, 500, 50);

  digitalWrite(stateGreen, LOW);
  digitalWrite(stateYellow, HIGH);
  digitalWrite(stateRed, LOW);
}

void stepRight() { // right motor off, left motor on
  digitalWrite(motorLeftA, HIGH);
  digitalWrite(motorLeftB, LOW);
  //digitalWrite(motorRightA, LOW);
  digitalWrite(motorRightB, LOW);

  tone(motorRightA, 500, 50);

  digitalWrite(stateGreen, LOW);
  digitalWrite(stateYellow, HIGH);
  digitalWrite(stateRed, LOW);
}

void stepHalt() { // both motors off
  digitalWrite(motorLeftA, LOW);
  digitalWrite(motorLeftB, LOW);
  digitalWrite(motorRightA, LOW);
  digitalWrite(motorRightB, LOW);

  digitalWrite(stateGreen, LOW);
  digitalWrite(stateYellow, LOW);
  digitalWrite(stateRed, HIGH);
}


#include <AFMotor.h>
#include <SoftwareSerial.h>

AF_DCMotor motor1(1);  //Back Left Wheel
AF_DCMotor motor2(2);  //Back Right Wheel
AF_DCMotor motor3(3);  //Front Right Wheel
AF_DCMotor motor4(4);  //Front Left Wheel

SoftwareSerial HC06(11, 10);  // declare the hc06 module to be used with and serial communication
int speed = 96;
String receivedSpeed = "";

void setup() {
  Serial.begin(9600);
  HC06.begin(9600);
  //Set Motor Speed
  motor1.setSpeed(speed);
  motor2.setSpeed(speed);
  motor3.setSpeed(speed);
  motor4.setSpeed(speed);
}


void loop() {
  while (HC06.available()) {
    delay(50);
    char c = HC06.read();
    Serial.print("Received command: ");
    Serial.println(c);
    if (isAlpha(c))
      processCommand(c);
    else
      setSpeed(c);
  }
}

void moveForward() {
  motor1.run(FORWARD);
  motor2.run(FORWARD);
  motor3.run(BACKWARD);
  motor4.run(BACKWARD);
}

void moveBackward() {
  motor1.run(BACKWARD);
  motor2.run(BACKWARD);
  motor3.run(FORWARD);
  motor4.run(FORWARD);
}

void moveLeft() {
  motor1.run(FORWARD);
  motor2.run(BACKWARD);
  motor3.run(BACKWARD);
  motor4.run(FORWARD);
}

void moveRight() {
  motor1.run(BACKWARD);
  motor2.run(FORWARD);
  motor3.run(FORWARD);
  motor4.run(BACKWARD);
}

void moveForwardLeft() {
  motor1.run(FORWARD);
  motor2.run(RELEASE);
  motor3.run(BACKWARD);
  motor4.run(RELEASE);
}

void moveForwardRight() {
  motor1.run(RELEASE);
  motor2.run(FORWARD);
  motor3.run(RELEASE);
  motor4.run(BACKWARD);
}

void moveBackwardLeft() {
  motor1.run(RELEASE);
  motor2.run(BACKWARD);
  motor3.run(RELEASE);
  motor4.run(FORWARD);
}

void moveBackwardRight() {
  motor1.run(BACKWARD);
  motor2.run(RELEASE);
  motor3.run(FORWARD);
  motor4.run(RELEASE);
}

void rotateLeft() {
  motor1.run(BACKWARD);
  motor2.run(FORWARD);
  motor3.run(BACKWARD);
  motor4.run(FORWARD);
}

void rotateRight() {
  motor1.run(FORWARD);
  motor2.run(BACKWARD);
  motor3.run(FORWARD);
  motor4.run(BACKWARD);
}

void stopMotors() {
  motor1.run(RELEASE);
  motor2.run(RELEASE);
  motor3.run(RELEASE);
  motor4.run(RELEASE);
}

void setSpeed(char c) {
  if (isdigit(c)) 
    receivedSpeed += c;

  else if (c == '\n') {  // Check for end of sequence (newline character)
    // If newline character is received, convert the sequence to an integer and set the speed
    speed = receivedSpeed.toInt();  // Convert string to integer
    motor1.setSpeed(speed);
    motor2.setSpeed(speed);
    motor3.setSpeed(speed);
    motor4.setSpeed(speed);  
    
    // Reset received speed sequence for the next command
    receivedSpeed = "";
  }
}

// Function to process received command
void processCommand(char command) {
  switch (command) {
    case 'F':
      moveForward();
      break;
    case 'B':
      moveBackward();
      break;
    case 'L':
      moveLeft();
      break;
    case 'R':
      moveRight();
      break;
    case 'G':
      moveForwardLeft();
      break;
    case 'H':
      moveForwardRight();
      break;
    case 'I':
      moveBackwardLeft();
      break;
    case 'J':
      moveBackwardRight();
      break;
    case 'C':
      rotateLeft();
      break;
    case 'D':
      rotateRight();
      break;
    case 'S':
      stopMotors();
      break;
    default:
      break;
  }
}


/*moveForward();
  delay(2000);
  stopMotors();
  delay(1000);

  moveBackward();
  delay(2000);
  stopMotors();
  delay(1000);


  moveLeft();
  delay(2000);
  stopMotors();
  delay(1000);


  moveRight();
  delay(2000);
  stopMotors();
  delay(1000);

  moveForwardLeft();
  delay(2000);
  stopMotors();
  delay(1000);

  moveForwardRight();
  delay(2000);
  stopMotors();
  delay(1000);

  moveBackwardLeft();
  delay(2000);
  stopMotors();
  delay(1000);

  moveBackwardRight();
  delay(2000);
  stopMotors();
  delay(1000);

  rotateLeft();
  delay(2000);
  stopMotors();
  delay(1000);

  rotateRight();
  delay(2000);
  stopMotors();
  delay(1000);
*/

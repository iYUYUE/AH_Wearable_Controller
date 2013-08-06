/*
  Analog input, analog output, serial output
 
 Reads an analog input pin, maps the result to a range from 0 to 255
 and uses the result to set the pulsewidth modulation (PWM) of an output pin.
 Also prints the results to the serial monitor.
 
 The circuit:
 * potentiometer connected to analog pin 0.
   Center pin of the potentiometer goes to the analog pin.
   side pins of the potentiometer go to +5V and ground
 * LED connected from digital pin 9 to ground
 
 created 29 Dec. 2008
 modified 9 Apr 2012
 by Tom Igoe
 
 This example code is in the public domain.
 
 */

// These constants won't change.  They're used to give names
// to the pins used:
const int analogInPinY = A0;  // Analog input pin that the potentiometer is attached to
const int analogInPinX = A1;  // Analog input pin that the potentiometer is attached to
const int analogOutPinY = 7; // Analog output pin that the LED is attached to
const int analogOutPinX = 6; // Analog output pin that the LED is attached to
const int digitalInPinZ = 2;
const int digitalOutPinZ = 5;

int sensorValueY = 0;        // value read from the pot
int outputValueY = 0;        // value output to the PWM (analog out)
int sensorValueX = 0;        // value read from the pot
int outputValueX = 0;        // value output to the PWM (analog out)
int outputValueZ = 1;


void setup() {
  // initialize serial communications at 9600 bps:
  Serial1.begin(57600, SERIAL_8O1); 
  pinMode(digitalOutPinZ, OUTPUT);
  pinMode(digitalInPinZ, INPUT);
}

void loop() {
  // read the analog in value:
  sensorValueY = analogRead(analogInPinY); 
  sensorValueX = analogRead(analogInPinX);
  outputValueZ = digitalRead(digitalInPinZ);  
  // map it to the range of the analog out:
  outputValueY = map(sensorValueY, 0, 1023, 0, 63);  
  outputValueX = map(sensorValueX, 0, 1023, 0, 63);  
  // change the analog out value:
  analogWrite(analogOutPinY, outputValueY);  
  analogWrite(analogOutPinX, outputValueX); 

  if(digitalRead(digitalInPinZ)){
    digitalWrite(digitalOutPinZ, LOW);
  }
  else{
    digitalWrite(digitalOutPinZ, HIGH);
  }
      

  // print the results to the serial monitor:  
  Serial1.print(outputValueY, DEC);  
  Serial1.print("," );              
  Serial1.print(outputValueX, DEC);
  Serial1.print(","); 

  // wait 2 milliseconds before the next loop
  // for the analog-to-digital converter to settle
  // after the last reading:
  delay(50);                     
}

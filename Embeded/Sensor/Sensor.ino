#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <HttpClient.h>

#define SENSOR_THRESHOLD 1500

const char* ssid = "your_id";
const char* password =  "your_password";                 // wifi 비번 
const String serverName = "http://copytixe.iptime.org:8080/climbing/";  // 웹서버주소
int value;
int sensor_number = 1;   // 기기 번호
int finish_sensor_number = 2;
int analog = 2;  

const int startPin = 18;
const int finishPin = 15;
const int ledPin = 43;    // the number of the LED pin

int ledState = 0;
bool startState = false;
bool finishState = false;

bool isStarting = false;
bool isFinishing = false;

void sendStart();
void sendSuccess();
bool sense(int senseNo);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial.println("Start");

  pinMode(ledPin, OUTPUT);
  pinMode(startPin, INPUT_PULLUP);
  pinMode(finishPin, INPUT_PULLUP);

  //led on until wifi connection finish
  digitalWrite(ledPin, HIGH);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) { //Check for the connection
    delay(1000);
    Serial.println("Connecting to WiFi..");
  }
  
 
  Serial.println("Connected to the WiFi network");
}

void loop() {
  // put your main code here, to run repeatedly:

  if(WiFi.status()== WL_CONNECTED){
    startState = sense(startPin);
    finishState = sense(finishPin);
    if(startState){
      ledState = 1;
      digitalWrite(ledPin, LOW);    // Turn the RGB LED off
      isStarting = startState;
      while(isStarting){
        isStarting = sense(startPin);
        delay(5);
      }
      sendStart();
    } else if(finishState){
      ledState = 1;
      digitalWrite(ledPin, LOW);    // Turn the RGB LED off
      sendSuccess();
      isFinishing = finishState;
      while(isFinishing){
        isFinishing = sense(finishPin);
        delay(100);
      }
    }
    digitalWrite(ledPin, HIGH);    // Turn the RGB LED off
  }
  else{
    Serial.println("Error in WiFi connection");

    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) { //Check for the connection
      delay(1000);
      Serial.println("Connecting to WiFi..");
    }
  }
}

void sendStart(){
  HTTPClient http;
  String uri = serverName + String(sensor_number) + "/start";
  http.begin(uri);
  String httpRequestData = "";
  int httpResponseCode = http.POST(httpRequestData);
  if(httpResponseCode>0) {
    String response = http.getString();                       //Get the response to the request
    Serial.println(httpResponseCode);   //Print return code
    Serial.println("send start");       //Print
  } else {
    Serial.print("Error on sending POST: ");
    Serial.println(httpResponseCode);
  }
  http.end();  //Free resources
}
void sendSuccess(){
  HTTPClient http;
  String uri = serverName + String(finish_sensor_number) + "/success";
  http.begin(uri);
  String httpRequestData = "";
  int httpResponseCode = http.PATCH(httpRequestData);
  if(httpResponseCode>0) {
    String response = http.getString();                       //Get the response to the request
    Serial.println(httpResponseCode);   //Print return code
    Serial.println("Send success");
  } else {
    Serial.print("Error on sending POST: ");
    Serial.println(httpResponseCode);
  }
  http.end();  //Free resources
}

bool sense(int sensePin){
  int analogReading = analogRead(sensePin);
  return (analogReading>SENSOR_THRESHOLD);
}

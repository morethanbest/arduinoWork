#include <Arduino_FreeRTOS.h>

#define LED_RED 13
#define BUZZER 12
#define SENSOR A0
#define SWITCH A1

#define FILTER_MEDIAN 101
#define FILTER_MEAN 100
#define FILTER_SHAKE 4



// define two tasks for Blink & AnalogRead
void TaskSensor( void *pvParameters );
void TaskFiltering( void *pvParameters );
void TaskBuzzer( void *pvParameters );

int meanFilterlter();
int medianFilter();
int shakeFilter();

int filter_buf[FILTER_MEDIAN];

int filter_mean[FILTER_MEAN];

int shakeValue = 0;
int newValue = 0;
int count = 0;


void setup() {

  xTaskCreate(
    TaskSensor
    ,  (const portCHAR *)"Sensor"   
    ,  128
    ,  NULL
    ,  3
    ,  NULL );

  xTaskCreate(
    TaskFiltering
    ,  (const portCHAR *) "Filtering"
    ,  128
    ,  NULL
    ,  2
    ,  NULL );

  xTaskCreate(
    TaskBuzzer
    ,  (const portCHAR *) "Switch"
    ,  128
    ,  NULL
    ,  1
    ,  NULL );

}

void loop()
{
}

/*--------------------------------------------------*/
/*---------------------- Tasks ---------------------*/
/*--------------------------------------------------*/
String command;
void TaskSensor(void *pvParameters) 
{
  (void) pvParameters;

  pinMode(LED_RED, OUTPUT);

  for (;;) 
  {
    if (Serial.available() > 0){
      while (Serial.available() > 0)//循环串口是否有数据
      {
        command += char(Serial.read());//叠加数据到comdata
        delay(2);//延时等待响应
      }
      if (command.length() > 0)//如果comdata有数据
      {
        if (command == "h")//如果comdata有数据
      {
        digitalWrite(LED_RED, HIGH); 
      }else if (command == "l")//如果comdata有数据
      {
        digitalWrite(LED_RED, LOW); 
      }
      command= "";
      }
    }
    vTaskDelay( 1000 / portTICK_PERIOD_MS ); 
  }
}

void TaskFiltering(void *pvParameters) 
{
  (void) pvParameters;

  Serial.begin(9600);

  for (;;)
  {
    int i;
    for(i = 0;i < FILTER_MEAN;i++){
      filter_mean[i] = analogRead(SENSOR);
    }
//    Serial.print(analogRead(SENSOR));
//    Serial.print(",");
    Serial.println(meanFilter());
      
    vTaskDelay(10); 
  }
}

void TaskBuzzer(void *pvParameters)
{
  (void) pvParameters;

  pinMode(BUZZER, OUTPUT);

  for (;;)
  {
    int sensorValue = analogRead(SENSOR);
    if(sensorValue < 100)
      digitalWrite(BUZZER, LOW);
    else
      digitalWrite(BUZZER, HIGH);
    vTaskDelay(1000 / portTICK_PERIOD_MS ); 
  }
}

double meanFilter() {
  int j;
  double sum = 0;
  for(j = 0; j < FILTER_MEAN; j++) {
      sum += filter_mean[j];
  }
  return sum / FILTER_MEAN;
}

int medianFilter() {
  int i, j;
  int filter_temp;
  for(j = 0; j < FILTER_MEDIAN - 1; j++) {
    for(i = 0; i < FILTER_MEDIAN - 1 - j; i++) {
      if(filter_buf[i] > filter_buf[i + 1]) {
        filter_temp = filter_buf[i];
        filter_buf[i] = filter_buf[i + 1];
        filter_buf[i + 1] = filter_temp;
      }
    }
  }
  return filter_buf[(FILTER_MEDIAN - 1) / 2];
}


int shakeFilter() {
  if(shakeValue != newValue) {
    count++;
    if(count > FILTER_SHAKE) {
      count = 0;
      shakeValue = newValue;
    }
  }
  else
    count = 0;
  return shakeValue;
}

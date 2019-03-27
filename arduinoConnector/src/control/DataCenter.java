package control;


import http.HttpClient;
import mqtt.SendMqttMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import serial.SimpleRead;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class DataCenter implements Runnable {


    private Date lastLightDate;

    private BlockingQueue conQueue;

    private SendMqttMessage sendMqttMessage;

    public DataCenter(BlockingQueue publicBoxQueue) {
        this.conQueue = publicBoxQueue;
        try {
            sendMqttMessage = new SendMqttMessage("arduinoConnector");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        this.run();
    }
    @Override
    public void run() {
        while (true) {
            if(!conQueue.isEmpty())
                try {
                    String str = conQueue.take().toString();
                    String[] data = str.split(",");
                    System.out.println("data center get: light " + data[0] + " temperature " + data[1]);
                    processLight(data[0]);
                    HttpClient.doGet("http://localhost:8803/temperature?t=" + data[1]);
                    sendMqttMessage.publish(data[0], "light");
//                    sendMqttMessage.publish(data[1], "temperat");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private void processLight(String lightStr){
        Double light;
        try {
            light = Double.parseDouble(lightStr);
        }catch (NumberFormatException e){
            return;
        }
        if (light > 800) {
            if(null == lastLightDate)
                lastLightDate = new Date();
            else if (new Date().getTime() - lastLightDate.getTime() > 5000){
                SimpleRead.sendComm("h");
            }
        } else {
            SimpleRead.sendComm("l");
            lastLightDate = null;
        }
    }
}

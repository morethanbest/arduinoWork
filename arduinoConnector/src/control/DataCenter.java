package control;


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
                    System.out.println("data center get:" + str);
                    processLight(str);
                    sendMqttMessage.publish(str, "light");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MqttException e) {
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

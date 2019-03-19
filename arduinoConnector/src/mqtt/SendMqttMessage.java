package mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * 发送数据到mqtt服务器
 *  * @author：涂有
 *  * @date 2017年8月16日 下午11:15:22
 */
public class SendMqttMessage {

    private static int qos = 2; //只有一次
    private static String broker = "tcp://127.0.0.1:1883";
    private static String userName = "ardunioConnector";
    private static String passWord = "tuyou";
    private MqttClient mqttClient;

    public SendMqttMessage(String clientId) throws MqttException {
        this.mqttClient = init(clientId, userName, passWord);
    }

    private MqttClient init(String clientId, String userName,
                            String password) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(userName);
        connOpts.setPassword(password.toCharArray());
        connOpts.setConnectionTimeout(10);
        connOpts.setKeepAliveInterval(20);
        MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
        mqttClient.setCallback(new PushCallback("test"));
        mqttClient.connect(connOpts);
        return mqttClient;
    }

    public void publish(String msg, String topic) throws MqttException {
        if (mqttClient != null) {
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(qos);
            message.setRetained(false);
            mqttClient.publish(topic, message);
            System.out.println("pub-->" + msg);
        }
    }

    public void disconnect() throws MqttException {
        if (mqttClient != null) {
            mqttClient.disconnect();
        }
    }
}

class PushCallback implements MqttCallback {
    private String threadId;

    public PushCallback(String threadId) {
        this.threadId = threadId;
    }

    public void connectionLost(Throwable cause) {

    }

    public void deliveryComplete(IMqttDeliveryToken token) {
//       System.out.println("deliveryComplete---------" + token.isComplete());
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String msg = new String(message.getPayload());
        System.out.println(threadId + " " + msg);
    }
}

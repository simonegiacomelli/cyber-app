package cyber.bletarget;

import android.util.Log;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

class MqttPaho implements Mqtt {

    private MqttClient client;

    @Override
    public void connect() {
        try {
            client = new MqttClient("ssl://mqtt.flespi.io", UUID.randomUUID().toString(), new MemoryPersistence());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("3s897csODyMGcSwQ75LY7uTipFBIBnzsDvrBvHfZ6Pt6xQMsLnhGH0PVvetUrQcU");
        options.setPassword(new char[0]);
        options.setAutomaticReconnect(true);


        try {
            client.connect(options);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void disconnect() {
        try {
            client.disconnect();
        } catch (Exception ex) {
            Log.e("TAG1", "error mqtt disconnect()", ex);
        }
    }

    @Override
    public void publish(String queueName, String payload) {
        try {
            client.publish(queueName, payload.getBytes(), 1, false);
        } catch (MqttException e) {
            Log.e("TAG1", "publish", e);
        }

    }
}

package cyber.bletarget;

import android.util.Log;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;

import java.util.UUID;

class MqttRssi {

    private Mqtt5BlockingClient client;

    void connect() {
        client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("mqtt.flespi.io")
                .simpleAuth(Mqtt5SimpleAuth.builder().username("3s897csODyMGcSwQ75LY7uTipFBIBnzsDvrBvHfZ6Pt6xQMsLnhGH0PVvetUrQcU").build())
                .buildBlocking();

        client.connect();

    }

    void disconnect() {
        try {
            client.disconnect();
        } catch (Exception ex) {
            Log.e("TAG1", "error mqtt disconnect()", ex);
        }
    }

    void publish(String queueName, String payload) {
        client.publishWith()
                .topic(queueName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(payload.getBytes())
                .send();
    }
}

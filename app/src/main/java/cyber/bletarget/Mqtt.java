package cyber.bletarget;

interface Mqtt {
    void connect();

    void disconnect();

    void publish(String queueName, String payload);
}

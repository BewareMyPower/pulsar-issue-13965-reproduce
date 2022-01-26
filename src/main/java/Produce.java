import java.util.Random;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

public class Produce {
    public static void main(String[] args) throws PulsarClientException {
        final String topicNamePrefix = "KeySharedConsumerTest-multi-topics";
        final PulsarAdmin admin = PulsarAdmin.builder().serviceHttpUrl("http://localhost:8080").build();
        for (int i = 0; i < 3; i++) {
            try {
                admin.topics().delete(topicNamePrefix + i);
            } catch (PulsarAdminException e) {
                System.out.println("Failed to delete " + topicNamePrefix + i + ": " + e.getMessage());
            }
        }
        admin.close();

        final PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build();
        final Random random = new Random();
        for (int i = 0; i < 3; i++) {
            final Producer<String> producer = client.newProducer(Schema.STRING)
                    .topic(topicNamePrefix + i)
                    .enableBatching(false)
                    .create();
            for (int j = 0; j < 1000; j++) {
                final int key = random.nextInt(100);
                producer.newMessage(Schema.STRING).value("msg-" + j).key(key + "").sendAsync();
            }
            producer.flush();
        }
        client.close();
    }
}

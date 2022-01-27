import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;

public class ProduceAndConsume {
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
        final List<Producer<String>> producers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            producers.add(client.newProducer(Schema.STRING)
                    .topic(topicNamePrefix + i)
                    .enableBatching(false)
                    .create());
        }

        final List<Consumer<String>> consumers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            consumers.add(client.newConsumer(Schema.STRING)
                    .topicsPattern(".*KeySharedConsumerTest-multi-topics.*")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionType(SubscriptionType.Key_Shared)
                    .subscriptionName("sub")
                    .subscribe());
        }

        final Random random = new Random();
        for (Producer<String> producer : producers) {
            for (int i = 0; i < 1000; i++) {
                final int key = random.nextInt(100);
                producer.newMessage(Schema.STRING).value("msg-" + i).key(key + "").sendAsync();
            }
            producer.flush();
        }

        final List<Integer> numReceivedList = new ArrayList<>();
        int numTotal = 0;
        for (final Consumer<String> consumer : consumers) {
            int n = 0;
            while (true) {
                final Message<String> msg = consumer.receive(1, TimeUnit.SECONDS);
                if (msg == null) {
                    break;
                }
                n++;
                //System.out.println(i + " receives " + msg.getValue()
                //        + " from " + msg.getMessageId() + " key is " + msg.getKey());
            }
            numReceivedList.add(n);
            numTotal += n;
        }
        numReceivedList.forEach(System.out::println);
        System.out.println(numTotal);
        client.close();
    }
}

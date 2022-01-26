import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;

public class Consume {
    public static void main(String[] args) throws PulsarClientException {
        final PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build();
        final List<Consumer<String>> consumers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            consumers.add(client.newConsumer(Schema.STRING)
                    .topicsPattern(".*KeySharedConsumerTest-multi-topics.*")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionType(SubscriptionType.Key_Shared)
                    .subscriptionName("sub")
                    .subscribe());
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

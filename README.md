# pulsar-issue-13965-reproduce

https://github.com/apache/pulsar/issues/13965

Before following steps, run `mvn clean compile` to build the project. And run a Pulsar standalone with `subscriptionKeySharedUseConsistentHashing=true`.

## How to reproduce

1. Run 3 producers to send 3000 messages to 3 topics with the same topic prefix

   ```bash
   mvn exec:java -Dexec.mainClass='Produce'
   ```

2. Run 3 consumers to subscribe with regex pattern and `Key_Shared` subscription. 

   ```bash
   mvn exec:java -Dexec.mainClass='Consume'
   ```

Since no acknowledgements are done in step 2, we can repeat step 2 and here are the output of 5 results in my local env:

```
1337
0
0
1337
```

```
3000
0
0
3000
```

```
3000
0
0
3000
```

```
3000
0
0
3000
```

```
3000
0
0
3000
```

The result of first time is wrong.

You can modify the subscription name, subscription type and receive timeout in `Consume.java` to see more results.

If you only want to run consumer once, you can just combine the steps in previous section:

```bash
mvn exec:java -Dexec.mainClass='ProduceAndConsume'
```

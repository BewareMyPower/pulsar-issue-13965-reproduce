# pulsar-issue-13965-reproduce

https://github.com/apache/pulsar/issues/13965

## How to reproduce

Before following steps, run `mvn clean compile` to build the project.

1. Run a Pulsar standalone with `subscriptionKeySharedUseConsistentHashing=true`.
2. Run
3.

1. Run a Pulsar standalone with `subscriptionKeySharedUseConsistentHashing=true`.

2. Run 3 producers to send 3000 messages to 3 topics with the same topic prefix

   ```bash
   mvn exec:java -Dexec.mainClass='Produce'
   ```

3. Run 3 consumers to subscribe with regex pattern and `Key_Shared` subscription. 

   ```bash
   mvn exec:java -Dexec.mainClass='Consume'
   ```

Here are the output of 5 results in my local env:

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

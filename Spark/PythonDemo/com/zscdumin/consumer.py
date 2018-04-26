from kafka import KafkaConsumer

consumer = KafkaConsumer('result')
for msg in consumer:
    print((msg.value).decode('utf8'))
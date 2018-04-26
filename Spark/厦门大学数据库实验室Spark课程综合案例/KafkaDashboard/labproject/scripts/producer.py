# coding: utf-8
from kafka import KafkaProducer
import csv
import time

producer = KafkaProducer(bootstrap_servers='localhost:9092')
csvfile = open("../data/user_log.csv","r")
reader = csv.reader(csvfile)

for line in reader:
    gender = line[9]
    if gender == 'gender':
        continue
    print(line[9])
    time.sleep(0.1)
    producer.send('sex',line[9].encode('utf8'))

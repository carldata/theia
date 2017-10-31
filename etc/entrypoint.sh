#!/bin/sh
java -Dlogback.configurationFile=/root/gelf.xml  -jar /root/theia.jar --kafka=$Kafka_Broker --eps=$Events_Per_Second  --cps=$Channels --statsDHost=$StatsD_Host


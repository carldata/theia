#!/bin/sh
java -Dlogback.configurationFile=/root/logback_deployment.xml  -jar /root/theia.jar --kafka=$Kafka_Broker --eps=$Events_Per_Second  --channels=$Channels_Per_Second --statSDHost=$StatSD_Host


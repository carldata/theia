#!/bin/sh
java -Dlogback.configurationFile=/root/gelf.xml  -jar /root/theia.jar --kafka=$KAFKA_BROKER --eps=$EPS  --channels=$CHANNELS --statsDHost=$STATSD

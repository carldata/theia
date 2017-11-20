#!/bin/sh
java -Dlogback.configurationFile=/root/gelf.xml  -jar /root/theia.jar --kafka=$KAFKA_BROKERS --eps=$EPS  --channels=$CHANNELS --statsd=$STATSD

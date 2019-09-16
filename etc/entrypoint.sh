#!/bin/sh
java -Dlogback.configurationFile=/root/gelf.xml  -jar /root/theia.jar --elasticSearchUrl=$ELASTIC_SEARCH_URL --elasticSearchPort=$ELASTIC_SEARCH_PORT --kafka=$KAFKA_BROKERS --eps=$EPS  --channels=$CHANNELS --statsd=$STATSD

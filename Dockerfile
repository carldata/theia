FROM openjdk:jdk-alpine

ENV SCALA_VERSION 2.12.3
ENV Kafka_Broker localhost:9092
ENV Events_Per_Second 100
ENV Channels 10
ENV StatsD_Host localhost

WORKDIR /root
ADD target/scala-2.12/theia.jar /root/theia.jar
ADD etc/gelf.xml /root/gelf.xml
ADD etc/entrypoint.sh /root/entrypoint.sh
ENTRYPOINT ["/bin/sh","/root/entrypoint.sh"]


FROM openjdk:jdk-alpine

ENV SCALA_VERSION 2.12.3
ENV KAFKA_BROKER localhost:9092
ENV EPS 10
ENV CHANNELS 10
ENV STATSD localhost

WORKDIR /root
ADD target/scala-2.12/theia.jar /root/theia.jar
ADD etc/gelf.xml /root/gelf.xml
ADD etc/entrypoint.sh /root/entrypoint.sh
ENTRYPOINT ["/bin/sh","/root/entrypoint.sh"]



FROM anapsix/alpine-java:8u151b12_jdk

MAINTAINER just@issboy

ADD ./streams-examples-3.3.0-jar-with-dependencies.jar /opt/

ENTRYPOINT ["java","-jar","-ea",
"-Dcom.sun.management.jmxremote.authenticate=false",
"-Dcom.sun.management.jmxremote.ssl=false",
"-Dcom.sun.management.jmxremote.port=9995",
"/opt/streams-examples-3.3.0-jar-with-dependencies.jar"]

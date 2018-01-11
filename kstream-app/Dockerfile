FROM anapsix/alpine-java:8u151b12_jdk
MAINTAINER just@issboy
ADD ./target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/
ENTRYPOINT ["java","-jar","/opt/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar"]

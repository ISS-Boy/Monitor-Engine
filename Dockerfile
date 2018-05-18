
FROM maven:3-jdk-8

MAINTAINER just@issboy


RUN set -ex && mv /etc/apt/sources.list /etc/apt/sources.list.bak && \
               echo "deb http://mirrors.163.com/debian/ jessie main non-free contrib" >/etc/apt/sources.list && \
               echo "deb http://mirrors.163.com/debian/ jessie-proposed-updates main non-free contrib" >>/etc/apt/sources.list && \
               echo "deb-src http://mirrors.163.com/debian/ jessie main non-free contrib" >>/etc/apt/sources.list && \
               echo "deb-src http://mirrors.163.com/debian/ jessie-proposed-updates main non-free contrib" >>/etc/apt/sources.list && \
               apt-get update && apt-get install -y --no-install-recommends nfs-common portmap

COPY . /root/workspace/engine
WORKDIR /root/workspace/engine
RUN set -ex && mvn clean package -Dmaven.test.skip=true

COPY docker-entrypoint.sh /usr/local/bin
COPY Server/target/server-1.0-SNAPSHOT.jar /root/dists/server.jar

# expose 8012, using -p 8012:8012 to connect container and the host-machine
EXPOSE 8012

# run the container with command --privileged=true to gain the CAP_SYS_ADMIN capability to mount nfs
# more to see https://stackoverflow.com/questions/39922161/mounting-nfs-shares-inside-docker-container
ENTRYPOINT ["docker-entrypoint.sh"]

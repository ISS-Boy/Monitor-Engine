#!/usr/bin/env bash

NFS_PATH=/mnt/nfs
NFS_ADDRESS=192.168.222.232:/data/nfs

mkdir $NFS_PATH

echo "mount nfs under /mnt/nfs"
mount -o nolock -t nfs $NFS_ADDRESS $NFS_PATH


echo "start server"
nohup java -jar \
     -Xms512M \
     -Xmx512M \
     -Dserver.port=8012 \
     -Djar-path=/root/workspace/engine/kstream-app/target/kstream-app-template-1.0-SNAPSHOT-jar-with-dependencies.jar \
     -Davro-path=/root/workspace/engine/Server/src/main/resources/avros \
     /root/dists/server.jar > /dev/null 2>&1 &



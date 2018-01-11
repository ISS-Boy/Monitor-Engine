package cn.issboy.mengine.server;

import cn.issboy.mengine.core.MEngine;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.swarm.*;

/**
 * created by just on 18-1-4
 */
public class MRestServer {

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            DockerClient dockerClient = DefaultDockerClient.fromEnv().build();

            new MEngine(dockerClient).buildJar(MRestServer.class.getClassLoader().getResourceAsStream("monitor/MonitorExample.xml"));

            System.out.println("total time :" + (System.currentTimeMillis()-start));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

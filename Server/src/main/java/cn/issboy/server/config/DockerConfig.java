package cn.issboy.server.config;

/**
 * created by just on 18-5-1
 */
public class DockerConfig {
    private final String dockerPort;
    private final String jarName;
    private final String commandArgs;

    public DockerConfig(String dockerPort, String jarName, String commandArgs) {
        this.dockerPort = dockerPort;
        this.jarName = jarName;
        this.commandArgs = commandArgs;
    }
}

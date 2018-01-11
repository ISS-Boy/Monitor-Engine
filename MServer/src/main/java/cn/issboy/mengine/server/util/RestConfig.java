//package cn.issboy.mengine.server.util;
//
//import com.github.dockerjava.api.command.DockerCmdExecFactory;
//import com.github.dockerjava.core.DefaultDockerClientConfig;
//import com.github.dockerjava.core.DockerClientConfig;
//import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
//import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
//
///**
// * created by just on 18-1-10
// */
//public class RestConfig {
//
//    public static final String REGISTRY_USER = "";
//    public static final String REGISTRY_PASS = "";
//    public static final String REGISTRY_MAIL = "";
//    public static final String REGISTRY_URL = "";
//
//
//
//
//    public static DockerClientConfig getDockerConfig(){
//        return DefaultDockerClientConfig.createDefaultConfigBuilder()
//                .withDockerHost("tcp://docker.somewhere.tld:2376")
//                .withDockerTlsVerify(true)
//                .withDockerCertPath("/home/user/.docker")
//                .withRegistryUsername(REGISTRY_USER)
//                .withRegistryPassword(REGISTRY_PASS)
//                .withRegistryEmail(REGISTRY_MAIL)
//                .withRegistryUrl(REGISTRY_URL)
//                .build();
//    }
//
//    public static DockerCmdExecFactory getDockerCmdExecFactory(){
//        return new JerseyDockerCmdExecFactory()
//                .withReadTimeout(1000)
//                .withConnectTimeout(1000)
//                .withMaxTotalConnections(100)
//                .withMaxPerRouteConnections(10);
//    }
//
//}

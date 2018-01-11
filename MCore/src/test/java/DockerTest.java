import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerConfigReader;
import com.spotify.docker.client.auth.ConfigFileRegistryAuthSupplier;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.swarm.ContainerSpec;
import com.spotify.docker.client.messages.swarm.ServiceSpec;
import com.spotify.docker.client.messages.swarm.TaskSpec;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;


/**
 * created by just on 18-1-10
 */
public class DockerTest {

    private static final String DOCKER_DIRECTORY = "../kstream-app";
    private static String IMG_NAME = "legendjust/test";
    private static

    DockerClient dockerClient;

    @Before
    public void initial() throws DockerCertificateException {

        dockerClient = DefaultDockerClient.fromEnv()
                .registryAuthSupplier(new ConfigFileRegistryAuthSupplier(new DockerConfigReader()))
                .build();
    }

    @Test
    public void swarmTest() throws DockerException, InterruptedException {

        ContainerSpec containerSpec = ContainerSpec.builder().image(IMG_NAME).build();

        TaskSpec taskSpec = TaskSpec.builder().containerSpec(containerSpec).build();

        ServiceSpec serviceSpec = ServiceSpec.builder().taskTemplate(taskSpec).build();

        dockerClient.createService(serviceSpec);

    }



    @Test
    public void buildImgTest(){

        final AtomicReference<String> imgIdFromMsg = new AtomicReference<>();

        try {
            final String retImgId = dockerClient.build(Paths.get(DOCKER_DIRECTORY), IMG_NAME,(message)->{
                final String imgId = message.id();
                if(imgId!= null){
                    imgIdFromMsg.set(imgId);
                }
            });
            System.out.println(retImgId);

            dockerClient.tag(retImgId, IMG_NAME);

            dockerClient.push(IMG_NAME);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}

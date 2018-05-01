package cn.issboy.server;

import cn.issboy.mengine.core.MEngine;
import cn.issboy.server.controller.SchemaFetcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class ServerApplication {


	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class,args);
        String path = context.getEnvironment().getProperty("avro-path");
	    fetch(path);
	}

	public static void fetch(String path){
        MEngine mEngine = MEngine.getSingletonEngine();


        Thread fetcher = new Thread(new SchemaFetcher(mEngine.getMetaStore(),path));
        fetcher.setName("schemaFetcher");
        fetcher.start();
    }


}

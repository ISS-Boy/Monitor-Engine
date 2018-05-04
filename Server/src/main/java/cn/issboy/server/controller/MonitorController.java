package cn.issboy.server.controller;

import cn.issboy.mengine.core.MEngine;
import cn.issboy.server.bean.Request;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * created by just on 18-3-28
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${into-topic}")
    private String intoTopic;
    @Value("${bootstrap-servers}")
    private String bootstrapServers;
    @Value("${schema-registry}")
    private String schemaRegistry;
    @Value("${nfs-path}")
    private String nfsPath;
    @Value("${jar-path}")
    private String jarPath;

    private MEngine mEngine = MEngine.getSingletonEngine();

    @RequestMapping(value = "/blocks", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> monitor(@RequestBody String requestStr) {
        try {
            Request request = JSONObject.parseObject(requestStr,Request.class);
            Map<String,Object> properties = initProps(request);
            String path = mEngine.buildJar(properties, request.getBlockGroup());

            HttpStatus status = path != null ? HttpStatus.OK : HttpStatus.NOT_ACCEPTABLE;
            return new ResponseEntity<>(path, status);
        } catch (Exception e) {
            logger.error("Failed to handle POST for : {}" , requestStr);
            e.printStackTrace();
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(e.getMessage(), status);
        }
    }

    public Map<String,Object> initProps(Request request){
        Map<String, Object> properties = new HashMap<>();

        properties.put("userId", request.getUserId());
        properties.put("monitorGroupId", request.getMonitorGroupId());
        properties.put("topic", intoTopic);
        properties.put("jarPath",jarPath);
        properties.put("bootstrapServers", bootstrapServers);
        properties.put("schemaRegistry", schemaRegistry);
        properties.put("nfsPath", nfsPath);
        return properties;
    }
}

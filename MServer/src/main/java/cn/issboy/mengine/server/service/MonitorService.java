package cn.issboy.mengine.server.service;

import cn.issboy.mengine.core.MEngine;
import cn.issboy.mengine.parser.pojo.Monitor;

/**
 * created by just on 18-1-8
 */
public class MonitorService {
    MEngine mEngine;

    public MonitorService(MEngine mEngine){
        this.mEngine = mEngine;
    }


    // TODO receive post request and return response.
    public void createMonitor(String fileName){
        Monitor monitor = mEngine.getXmlBinder(this.getClass().getClassLoader().getResourceAsStream("monitor/MonitorExample.xml"));



        pushToRegistry();
    }

    public void pushToRegistry(){
        //TODO interactive with docker


    }
}

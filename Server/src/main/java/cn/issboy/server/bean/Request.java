package cn.issboy.server.bean;


import cn.issboy.mengine.core.parser.BlockGroup;

/**
 * created by just on 18-3-28
 */

public class Request {

     String userId;
     String monitorGroupId;
     BlockGroup blockGroup;

     public Request(){}

    public Request(String userId, String monitorGroupId, BlockGroup blockGroup) {
        this.userId = userId;
        this.monitorGroupId = monitorGroupId;
        this.blockGroup = blockGroup;
    }

    public String getUserId() {
        return userId;
    }

    public String getMonitorGroupId() {
        return monitorGroupId;
    }

    public BlockGroup getBlockGroup() {
        return blockGroup;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMonitorGroupId(String monitorGroupId) {
        this.monitorGroupId = monitorGroupId;
    }

    public void setBlockGroup(BlockGroup blockGroup) {
        this.blockGroup = blockGroup;
    }
}

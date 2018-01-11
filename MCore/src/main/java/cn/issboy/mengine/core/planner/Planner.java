package cn.issboy.mengine.core.planner;

import cn.issboy.mengine.core.planner.plan.PlanNode;
import cn.issboy.mengine.core.planner.plan.ProjectNode;

/**
 * created by just on 18-1-3
 */
public class Planner {


    public Planner(){

    }

    public PlanNode buildPlan(){

        return buildeNode();
    }

    public PlanNode buildeNode(){
        return new ProjectNode();
    }
}

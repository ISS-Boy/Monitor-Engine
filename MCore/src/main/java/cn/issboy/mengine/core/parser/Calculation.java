/**
  * Copyright 2018 bejson.com 
  */
package cn.issboy.mengine.core.parser;
import java.util.List;

/**
 * Auto-generated: 2018-03-27 11:1:23
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Calculation extends Node{

    private Window window;
    private List<CalculationValues> calculationValues;
    public void setWindow(Window window) {
         this.window = window;
     }
     public Window getWindow() {
         return window;
     }

    public void setCalculationValues(List<CalculationValues> calculationValues) {
         this.calculationValues = calculationValues;
     }
     public List<CalculationValues> getCalculationValues() {
         return calculationValues;
     }

    @Override
    protected void accept(MonitorVisitor visitor) {
        visitor.visitCalculation(this);
    }

    @Override
    public String toString() {
        return "Calculation{" +
                "window=" + window +
                ", calculationValues=" + calculationValues +
                '}';
    }

}
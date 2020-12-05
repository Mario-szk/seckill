package com.jerusalem.seckill.dataobject;

/****
 * 实体类
 * @author jerusalem
 * @date 2020-04-15 08:30:52
 */
public class SequenceDO {

    private String name;
    private Integer currentValue;
    private Integer step;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
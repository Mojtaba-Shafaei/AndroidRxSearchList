package com.mojtaba_shafaei.android.lovSimpleSample;

import com.mojtaba_shafaei.android.LovSimple;

public class Job implements LovSimple.Item {
    String code;
    String des;
    int priority;

    public Job() {
    }

    public Job(String code, String des, int priority) {
        this.code = code;
        this.des = des;
        this.priority = priority;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDes() {
        return des;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Job{" +
                "code='" + code + '\'' +
                ", des='" + des + '\'' +
                ", priority=" + priority +
                '}';
    }
}

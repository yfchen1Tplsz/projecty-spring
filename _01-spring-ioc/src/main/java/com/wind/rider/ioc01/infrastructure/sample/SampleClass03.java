package com.wind.rider.ioc01.infrastructure.sample;

import lombok.Data;

@Data
public class SampleClass03 implements SampleInterface{
    private String name;
    public void init(){
        this.name = "initName";
    }
}

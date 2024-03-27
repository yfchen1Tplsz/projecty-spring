package com.wind.rider.ioc01.infrastructure.sample;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class SampleClass05 implements SampleInterface{

    @Autowired
    private SampleClass04 sampleClass04;
    private SampleClass01 sampleClass01;
}

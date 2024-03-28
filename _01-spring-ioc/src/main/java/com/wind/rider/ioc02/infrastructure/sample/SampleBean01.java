package com.wind.rider.ioc02.infrastructure.sample;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
public class SampleBean01 {
    @Autowired
    private final SampleBean02 sampleBean02;

    public SampleBean01(SampleBean02 sampleBean02) {
        this.sampleBean02 = sampleBean02;
        System.out.println("SampleBean01实例化：SampleBean01("+sampleBean02+")");
    }

    public SampleBean02 getSampleBean02() {
        return sampleBean02;
    }
}

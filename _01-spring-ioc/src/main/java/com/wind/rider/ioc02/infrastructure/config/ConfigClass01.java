package com.wind.rider.ioc02.infrastructure.config;

import com.wind.rider.ioc02.infrastructure.sample.SampleBean01;
import com.wind.rider.ioc02.infrastructure.sample.SampleBean02;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yfchen1
 * @date 2024/3/28
 */

@Configuration
public class ConfigClass01 {
    @Bean
    public SampleBean02 sampleBean02(){
        return new SampleBean02();
    }
    @Bean
    public SampleBean01 sampleBean01(SampleBean02 sampleBean02){
        return new SampleBean01(sampleBean02);
    }
}

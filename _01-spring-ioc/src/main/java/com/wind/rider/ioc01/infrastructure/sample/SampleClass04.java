package com.wind.rider.ioc01.infrastructure.sample;

import com.wind.rider.ioc01.DefaultListableBeanFactoryInterface;
import com.wind.rider.ioc01.infrastructure.annotation.MyFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@MyFlag(value = "123-type")
public class SampleClass04 implements SampleInterface {
    private static Logger log = LoggerFactory.getLogger(DefaultListableBeanFactoryInterface.class);

    @MyFlag(value = "message-field")
    private String message;

    public SampleClass04() {
        log.info("SampleClass04 实例化 ,class = " + this);
    }

    public SampleClass04(String message) {
        this.message = message;
        log.info("SampleClass04 实例化 ,message ={},class = {}",this.message,this);
    }

    @MyFlag(value = "init-method")
    public void initialize() {
        log.info("SampleClass04 初始化.");
    }
}

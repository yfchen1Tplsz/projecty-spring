package com.wind.rider.ioc01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Order01_DefaultListableBeanFactory {
    private static Logger log = LoggerFactory.getLogger(Order01_DefaultListableBeanFactory.class);

    public static void main(String[] args) {
        log.info("test slf4j-logback info");
        log.warn("test slf4j-logback warning");
        log.error("test slf4j-logback error");

    }
}

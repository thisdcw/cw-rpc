package com.cw.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ExampleSpringbootConsumerApplicationTests {

    @Resource
    private ExampleServiceImpl exampleService;

    @Test
    void contextLoads() {
    }

    @Test
    void test1() {
        exampleService.test();
    }

}

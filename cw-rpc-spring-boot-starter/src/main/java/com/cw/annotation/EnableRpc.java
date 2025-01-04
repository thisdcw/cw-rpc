package com.cw.annotation;

import com.cw.bootstrap.RpcConsumerBootstrap;
import com.cw.bootstrap.RpcInitBootstrap;
import com.cw.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用RPc注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要server
     *
     * @return
     */
    boolean needServer() default true;
}

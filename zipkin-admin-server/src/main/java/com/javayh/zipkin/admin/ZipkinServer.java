package com.javayh.zipkin.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import zipkin.server.internal.EnableZipkinServer;

/**
 * <p>
 * zipkin 启动类
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-05-22
 */
@EnableZipkinServer
@EnableDiscoveryClient
@SpringBootApplication(exclude = HttpEncodingAutoConfiguration.class)
public class ZipkinServer {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinServer.class, args);
    }

}

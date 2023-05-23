package com.javayh.zipkin.admin.config;

import com.javayh.zipkin.storage.postgresql.PostgreSQLStorage;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * <p>
 * 配置bean
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-05-19
 */
@Order
@Configuration
public class BeanConfig {


    @Bean
    public PostgreSQLStorage postgreSQLStorage(HikariDataSource dataSource) {
        return PostgreSQLStorage.builder().datasource(dataSource).executor(Runnable::run).build();
    }

}

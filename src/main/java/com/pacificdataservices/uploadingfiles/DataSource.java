package com.pacificdataservices.uploadingfiles;



import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

public class DataSource {


@Bean
@Primary
@ConfigurationProperties("app.datasource.member")
public DataSourceProperties memberDataSourceProperties() {
    return new DataSourceProperties();
}
@Bean
@Primary
@ConfigurationProperties("app.datasource.member.configuration")
public HikariDataSource memberDataSource() {
    return memberDataSourceProperties().initializeDataSourceBuilder()
            .type(HikariDataSource.class).build();

}}
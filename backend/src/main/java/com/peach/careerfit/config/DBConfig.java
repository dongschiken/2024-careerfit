package com.peach.careerfit.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.peach.careerfit.**.model.dao"})
public class DBConfig {

}

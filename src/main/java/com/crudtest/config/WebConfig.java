package com.crudtest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/email")
                .allowedOrigins("https://jeseogy.net");

        registry.addMapping("/posts/**")
                .allowedOrigins("*");
    }
}

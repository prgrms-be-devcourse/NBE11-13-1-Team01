package com.composebean.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebConfig(
            @Value("${file.upload-dir:uploads/products}")
            String uploadDir
    ) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {
        Path uploadPath = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(
                        uploadPath.toUri().toString()
                );
    }
}
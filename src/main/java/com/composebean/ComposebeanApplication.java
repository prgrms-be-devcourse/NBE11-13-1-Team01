package com.composebean;

import com.composebean.global.slack.SlackProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(SlackProperties.class)
@SpringBootApplication
public class ComposebeanApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComposebeanApplication.class, args);
    }

}

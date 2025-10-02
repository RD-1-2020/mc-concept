package org.azurecloud.solutions.pusa;

import org.azurecloud.solutions.pusa.config.PasswordPolicyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PasswordPolicyProperties.class)
public class PusaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PusaApplication.class, args);
    }

}

package org.azurecloud.solutions.bus.nats;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Nats;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bus.BusBridge;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({BusBridge.class, Connection.class})
@EnableConfigurationProperties({NatsBusProperties.class, BusProperties.class})
public class NatsBusAutoConfiguration {

    @Bean
    public Connection natsConnection(NatsBusProperties natsBusProperties) throws IOException, InterruptedException {
        return Nats.connect(natsBusProperties.getServerUrl());
    }

    @Bean
    public BusBridge natsBusBridge(ApplicationEventPublisher context,
                                   Connection natsConnection,
                                   NatsBusProperties natsBusProperties,
                                   BusProperties busProperties,
                                   ObjectMapper objectMapper) {
        return new NatsBusBridge(context, natsConnection, natsBusProperties, busProperties, objectMapper);
    }
}

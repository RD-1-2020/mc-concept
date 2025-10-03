package org.azurecloud.solutions.pusa.config;

import org.azurecloud.solutions.shared.props.ConfigurationServiceGrpc;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ConfigurationServiceGrpc.ConfigurationServiceBlockingStub configurationServiceStub(
            GrpcChannelFactory channelFactory) {
        return ConfigurationServiceGrpc.newBlockingStub(
                channelFactory.createChannel("default-channel")
        );
    }
}

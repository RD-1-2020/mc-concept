package org.azurecloud.solutions.pusa.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.azurecloud.solutions.shared.props.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @GrpcClient("phoca-service")
    private ConfigurationServiceGrpc.ConfigurationServiceBlockingStub configurationServiceStub;

    private final String applicationName;
    private final Environment environment;

    @Autowired
    public ConfigService(@Value("${spring.application.name}") String applicationName, Environment environment) {
        this.applicationName = applicationName;
        this.environment = environment;
    }

    public String getConfiguration(String key) {
        String profile = getActiveProfile();
        String label = "main";

        GetConfigurationRequest request = GetConfigurationRequest.newBuilder()
                .setKey(key)
                .setApplication(applicationName)
                .setProfile(profile)
                .setLabel(label)
                .build();
        GetConfigurationResponse response = configurationServiceStub.getConfiguration(request);
        return response.getValue();
    }

    public void updateConfiguration(String key, String value) {
        String profile = getActiveProfile();
        String label = "main";

        UpdateConfigurationRequest request = UpdateConfigurationRequest.newBuilder()
                .setKey(key)
                .setValue(value)
                .setApplication(applicationName)
                .setProfile(profile)
                .setLabel(label)
                .build();
        configurationServiceStub.updateConfiguration(request);
    }

    private String getActiveProfile() {
        if (environment.getActiveProfiles().length > 0) {
            return environment.getActiveProfiles()[0];
        }
        return "default";
    }
}

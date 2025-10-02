package org.azurecloud.solutions.pusa.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.azurecloud.solutions.shared.props.ConfigurationServiceGrpc;
import org.azurecloud.solutions.shared.props.GetConfigurationRequest;
import org.azurecloud.solutions.shared.props.GetConfigurationResponse;
import org.azurecloud.solutions.shared.props.UpdateConfigurationRequest;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @GrpcClient("phoca-service")
    private ConfigurationServiceGrpc.ConfigurationServiceBlockingStub configurationServiceStub;

    public String getConfiguration(String name) {
        GetConfigurationRequest request = GetConfigurationRequest.newBuilder().setName(name).build();
        GetConfigurationResponse response = configurationServiceStub.getConfiguration(request);
        return response.getValue();
    }

    public void updateConfiguration(String name, String value) {
        UpdateConfigurationRequest request = UpdateConfigurationRequest.newBuilder()
                .setName(name)
                .setValue(value)
                .build();
        configurationServiceStub.updateConfiguration(request);
    }
}

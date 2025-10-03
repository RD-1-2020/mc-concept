package org.azurecloud.solutions.phoca.grpc;

import io.grpc.stub.StreamObserver;
import org.azurecloud.solutions.shared.props.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@GrpcService
public class ConfigurationServiceImpl extends ConfigurationServiceGrpc.ConfigurationServiceImplBase {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher publisher;
    private final BusProperties busProperties;
    private final EnvironmentRepository environmentRepository;

    @Autowired
    public ConfigurationServiceImpl(JdbcTemplate jdbcTemplate, ApplicationEventPublisher publisher, BusProperties busProperties, EnvironmentRepository environmentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.publisher = publisher;
        this.busProperties = busProperties;
        this.environmentRepository = environmentRepository;
    }

    @Override
    public void getConfiguration(GetConfigurationRequest request, StreamObserver<GetConfigurationResponse> responseObserver) {
        org.springframework.cloud.config.environment.Environment environment = environmentRepository.findOne(
                request.getApplication(),
                request.getProfile(),
                request.getLabel()
        );

        String value = environment.getPropertySources().stream()
                .map(org.springframework.cloud.config.environment.PropertySource::getSource) // Get the underlying Map
                .filter(source -> source.containsKey(request.getKey()))
                .map(source -> source.get(request.getKey()))
                .map(String::valueOf)
                .findFirst()
                .orElse(null);

        GetConfigurationResponse.Builder responseBuilder = GetConfigurationResponse.newBuilder()
                .setKey(request.getKey())
                .setApplication(request.getApplication())
                .setProfile(request.getProfile())
                .setLabel(request.getLabel());

        if (value != null) {
            responseBuilder.setValue(value);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateConfiguration(UpdateConfigurationRequest request, StreamObserver<UpdateConfigurationResponse> responseObserver) {
        String sql = "UPDATE properties SET value = ? WHERE key = ? AND application = ? AND profile = ? AND label = ?";
        jdbcTemplate.update(sql, request.getValue(), request.getKey(), request.getApplication(), request.getProfile(), request.getLabel());

        publisher.publishEvent(new RefreshRemoteApplicationEvent(this,
                this.busProperties.getId(),
                request::getApplication));

        UpdateConfigurationResponse response = UpdateConfigurationResponse.newBuilder()
                .setKey(request.getKey())
                .setValue(request.getValue())
                .setApplication(request.getApplication())
                .setProfile(request.getProfile())
                .setLabel(request.getLabel())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

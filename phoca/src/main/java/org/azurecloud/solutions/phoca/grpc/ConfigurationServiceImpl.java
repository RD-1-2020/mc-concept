package org.azurecloud.solutions.phoca.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.azurecloud.solutions.shared.props.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;

@GrpcService
public class ConfigurationServiceImpl extends ConfigurationServiceGrpc.ConfigurationServiceImplBase {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher publisher;
    private final BusProperties busProperties;

    @Autowired
    public ConfigurationServiceImpl(JdbcTemplate jdbcTemplate, ApplicationEventPublisher publisher, BusProperties busProperties) {
        this.jdbcTemplate = jdbcTemplate;
        this.publisher = publisher;
        this.busProperties = busProperties;
    }

    @Override
    public void getConfiguration(GetConfigurationRequest request, StreamObserver<GetConfigurationResponse> responseObserver) {
        String sql = "SELECT VALUE FROM PROPERTIES WHERE KEY = ? AND APPLICATION = ? AND PROFILE = ? AND LABEL = ?";
        String value = jdbcTemplate.queryForObject(sql, new Object[]{request.getKey(), request.getApplication(), request.getProfile(), request.getLabel()}, String.class);

        GetConfigurationResponse response = GetConfigurationResponse.newBuilder()
                .setKey(request.getKey())
                .setValue(value)
                .setApplication(request.getApplication())
                .setProfile(request.getProfile())
                .setLabel(request.getLabel())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateConfiguration(UpdateConfigurationRequest request, StreamObserver<UpdateConfigurationResponse> responseObserver) {
        String sql = "UPDATE PROPERTIES SET VALUE = ? WHERE KEY = ? AND APPLICATION = ? AND PROFILE = ? AND LABEL = ?";
        jdbcTemplate.update(sql, request.getValue(), request.getKey(), request.getApplication(), request.getProfile(), request.getLabel());

        // Publish refresh event to the specific application that was changed
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

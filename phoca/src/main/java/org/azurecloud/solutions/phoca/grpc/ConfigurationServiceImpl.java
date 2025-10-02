package org.azurecloud.solutions.phoca.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.azurecloud.solutions.shared.props.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.ServiceMatcher;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;

@GrpcService
public class ConfigurationServiceImpl extends ConfigurationServiceGrpc.ConfigurationServiceImplBase {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher publisher;
    private final ServiceMatcher serviceMatcher;

    @Autowired
    public ConfigurationServiceImpl(JdbcTemplate jdbcTemplate, ApplicationEventPublisher publisher, ServiceMatcher serviceMatcher) {
        this.jdbcTemplate = jdbcTemplate;
        this.publisher = publisher;
        this.serviceMatcher = serviceMatcher;
    }

    @Override
    public void getConfiguration(GetConfigurationRequest request, StreamObserver<GetConfigurationResponse> responseObserver) {
        String sql = "SELECT VALUE FROM PROPERTIES WHERE KEY = ? AND APPLICATION = 'pusa' AND PROFILE = 'default' AND LABEL = 'main'";
        String value = jdbcTemplate.queryForObject(sql, new Object[]{request.getName()}, String.class);

        GetConfigurationResponse response = GetConfigurationResponse.newBuilder()
                .setName(request.getName())
                .setValue(value)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateConfiguration(UpdateConfigurationRequest request, StreamObserver<UpdateConfigurationResponse> responseObserver) {
        String sql = "UPDATE PROPERTIES SET VALUE = ? WHERE KEY = ? AND APPLICATION = 'pusa' AND PROFILE = 'default' AND LABEL = 'main'";
        jdbcTemplate.update(sql, request.getValue(), request.getName());

        // Publish refresh event to all services except self
        publisher.publishEvent(new RefreshRemoteApplicationEvent(this, serviceMatcher.getServiceId(), serviceMatcher.getMatcher(null)));


        UpdateConfigurationResponse response = UpdateConfigurationResponse.newBuilder()
                .setName(request.getName())
                .setValue(request.getValue())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

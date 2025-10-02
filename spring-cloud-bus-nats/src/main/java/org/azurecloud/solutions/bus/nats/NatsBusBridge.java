package org.azurecloud.solutions.bus.nats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bus.BusBridge;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

public class NatsBusBridge implements BusBridge {

    private static final Logger log = LoggerFactory.getLogger(NatsBusBridge.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final Connection natsConnection;
    private final NatsBusProperties natsBusProperties;
    private final BusProperties busProperties;
    private final ObjectMapper objectMapper;

    public NatsBusBridge(ApplicationEventPublisher applicationEventPublisher, Connection natsConnection, NatsBusProperties natsBusProperties, BusProperties busProperties, ObjectMapper objectMapper) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.natsConnection = natsConnection;
        this.natsBusProperties = natsBusProperties;
        this.busProperties = busProperties;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void subscribe() {
        Dispatcher dispatcher = natsConnection.createDispatcher(msg -> {
            try {
                String json = new String(msg.getData(), StandardCharsets.UTF_8);
                RemoteApplicationEvent event = objectMapper.readValue(json, RemoteApplicationEvent.class);

                // Ignore events from self
                if (!busProperties.getId().equals(event.getOriginService())) {
                    applicationEventPublisher.publishEvent(event);
                }
            } catch (Exception e) {
                log.error("Error processing message from NATS", e);
            }
        });
        dispatcher.subscribe(natsBusProperties.getDestination());
    }

    @Override
    public void send(RemoteApplicationEvent event) {
        try {
            if (!StringUtils.hasText(event.getDestinationService())) {
                String json = objectMapper.writeValueAsString(event);
                natsConnection.publish(natsBusProperties.getDestination(), json.getBytes(StandardCharsets.UTF_8));
            } else {
                 // Destination-specific logic can be added here if needed
                 log.warn("Destination-specific events are not yet implemented for NATS bus.");
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing event to JSON", e);
        }
    }
}

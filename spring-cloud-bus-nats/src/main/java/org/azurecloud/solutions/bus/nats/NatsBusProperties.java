package org.azurecloud.solutions.bus.nats;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.bus.nats")
public class NatsBusProperties {

    /**
     * NATS server URL.
     */
    private String serverUrl = "nats://localhost:4222";

    /**
     * The destination (subject) to which messages are sent.
     */
    private String destination = "spring.cloud.bus.events";

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}

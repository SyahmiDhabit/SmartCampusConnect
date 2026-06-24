package com.smartcampus.library.soap;

import jakarta.xml.ws.Endpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class SOAPEndpointPublisher implements SmartLifecycle {

    private final LibrarySOAPService soapService;
    private final String soapHost;
    private final int soapPort;
    private final String soapPath;

    private Endpoint endpoint;
    private boolean isRunning = false;
    private String publishedUrl;

    public SOAPEndpointPublisher(LibrarySOAPService soapService,
                                 @Value("${smartcampus.soap.host}") String soapHost,
                                 @Value("${smartcampus.soap.port}") int soapPort,
                                 @Value("${smartcampus.soap.path}") String soapPath) {
        this.soapService = soapService;
        this.soapHost = soapHost;
        this.soapPort = soapPort;
        this.soapPath = soapPath;
    }

    @Override
    public void start() {
        publishedUrl = "http://" + soapHost + ":" + soapPort + soapPath;
        System.out.println("[SOAP Publisher] Publishing JAX-WS SOAP service to: " + publishedUrl);
        try {
            endpoint = Endpoint.publish(publishedUrl, soapService);
            isRunning = true;
            System.out.println("[SOAP Publisher] WSDL at: " + publishedUrl + "?wsdl");
        } catch (Exception e) {
            System.err.println("[SOAP Publisher] Failed to publish SOAP service: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        if (endpoint != null) {
            endpoint.stop();
        }
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}

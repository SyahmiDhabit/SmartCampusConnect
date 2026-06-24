package com.smartcampus.shared.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MessageSender {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int TIMEOUT_MS = 2000; // 2 seconds timeout to prevent blocking

    /**
     * Sends a Message object to the specified TCP server.
     * Uses line-based framing (message payload followed by a newline '\n').
     */
    public static void sendMessage(String host, int port, Message message) {
        try (Socket socket = new Socket()) {
            // Establish connection with a timeout
            socket.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
            
            // Serialize Message to JSON
            String jsonPayload = objectMapper.writeValueAsString(message);
            
            // Send JSON payload followed by a newline character (Line-based framing)
            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                writer.println(jsonPayload);
                System.out.println("[TCP Client] Successfully sent event: " + message.getEventType() + " to " + host + ":" + port);
            }
        } catch (Exception e) {
            // Log warning but do not crash the calling thread (Graceful Degradation / Asynchronous mitigation)
            System.err.println("[TCP Client] Failed to send message to " + host + ":" + port + " - Error: " + e.getMessage());
        }
    }
}

package com.smartcampus.loadtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.smartcampus.shared.messaging.CampusEvent;
import com.smartcampus.shared.messaging.RabbitMqConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadTestClient {

    private static final String RESET_URL = "http://localhost:8083/api/notifications";
    private static final String COUNT_URL = "http://localhost:8083/api/notifications/count";
    private static final int CONCURRENT_REQUESTS = 50;
    private static final String RABBIT_HOST = System.getenv().getOrDefault("SPRING_RABBITMQ_HOST", "localhost");

    public static void main(String[] args) throws Exception {
        System.out.println("======================================================================");
        System.out.println("     SMARTCAMPUS CONNECT - RABBITMQ CONCURRENCY LOAD TESTER (R5)      ");
        System.out.println("======================================================================");

        System.out.println("[LoadTester] Resetting Notification Service logs via REST DELETE...");
        try {
            sendHttpDelete(RESET_URL);
            System.out.println("[LoadTester] Reset successful.");
        } catch (Exception e) {
            System.err.println("[LoadTester] Warning: Failed to reset notification service: " + e.getMessage());
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBIT_HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        ObjectMapper mapper = new ObjectMapper();
        ExecutorService threadPool = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_REQUESTS);
        long startTime = System.currentTimeMillis();

        System.out.println("[LoadTester] Publishing " + CONCURRENT_REQUESTS + " concurrent RabbitMQ events...");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(RabbitMqConstants.EXCHANGE, "topic", true);

            for (int i = 1; i <= CONCURRENT_REQUESTS; i++) {
                final int requestId = i;
                threadPool.submit(() -> {
                    try {
                        Map<String, Object> data = new LinkedHashMap<>();
                        data.put("threadId", requestId);
                        data.put("summary", "Concurrent load-test message from thread " + requestId);

                        CampusEvent event = CampusEvent.of(
                                RabbitMqConstants.ET_LOAD_TEST,
                                RabbitMqConstants.RK_LOAD_TEST,
                                "load-tester",
                                data
                        );

                        byte[] body = mapper.writeValueAsBytes(event);
                        channel.basicPublish(
                                RabbitMqConstants.EXCHANGE,
                                RabbitMqConstants.RK_LOAD_TEST,
                                null,
                                body
                        );
                    } catch (Exception e) {
                        System.err.println("[LoadTester] Publish failed for thread " + requestId + ": " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await(15, TimeUnit.SECONDS);
        threadPool.shutdown();
        System.out.println("[LoadTester] Publish phase completed in " + (System.currentTimeMillis() - startTime) + " ms.");

        Thread.sleep(3000);

        try {
            String countResponse = sendHttpGet(COUNT_URL);
            int finalCount = Integer.parseInt(countResponse.trim());

            System.out.println("\n--------------------------------------------------");
            System.out.println("CONCURRENCY PROTECTION ASSERTION RESULTS:");
            System.out.println("Expected Count: " + CONCURRENT_REQUESTS);
            System.out.println("Actual Count  : " + finalCount);

            if (finalCount == CONCURRENT_REQUESTS) {
                System.out.println("STATUS        : SUCCESS (Thread-Safety Confirmed!)");
                System.out.println("Explanation   : ReentrantLock protected shared counter while RabbitMQ listener pool processed events concurrently.");
            } else {
                System.out.println("STATUS        : FAILED (Race Condition Detected)");
            }
            System.out.println("--------------------------------------------------");
        } catch (Exception e) {
            System.err.println("[LoadTester] Error verifying results: " + e.getMessage());
        }
    }

    private static void sendHttpDelete(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("DELETE");
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        int respCode = conn.getResponseCode();
        conn.disconnect();
        if (respCode != 200 && respCode != 204) {
            throw new RuntimeException("HTTP response code: " + respCode);
        }
    }

    private static String sendHttpGet(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }
}

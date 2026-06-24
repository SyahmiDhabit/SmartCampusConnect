package com.smartcampus.enrolment.resilience;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleCircuitBreaker {

    public enum State {
        CLOSED,      // Normal operations - downstream service is healthy
        OPEN,        // Downstream failed; fail-fast immediately without making HTTP calls
        HALF_OPEN    // Cooldown elapsed; try single call to check if downstream has recovered
    }

    private final String serviceName;
    private final int failureThreshold;
    private final long cooldownDurationMs;

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastStateChangeTime = new AtomicLong(System.currentTimeMillis());

    public SimpleCircuitBreaker(String serviceName, int failureThreshold, long cooldownDurationMs) {
        this.serviceName = serviceName;
        this.failureThreshold = failureThreshold;
        this.cooldownDurationMs = cooldownDurationMs;
    }

    /**
     * Determines whether a call to the downstream service is permitted.
     */
    public boolean canCall() {
        State currentState = state.get();
        if (currentState == State.CLOSED) {
            return true;
        }

        if (currentState == State.OPEN) {
            long elapsedTime = System.currentTimeMillis() - lastStateChangeTime.get();
            if (elapsedTime > cooldownDurationMs) {
                // Cooldown elapsed; transition to HALF_OPEN to test downstream service
                if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                    lastStateChangeTime.set(System.currentTimeMillis());
                    System.out.println("[CircuitBreaker - " + serviceName + "] Cooldown period elapsed. Transitioning to HALF_OPEN.");
                }
                return true;
            }
            // Still in cooldown, fail fast
            return false;
        }

        // HALF_OPEN
        return true;
    }

    /**
     * Records a successful response from downstream.
     */
    public void recordSuccess() {
        State currentState = state.get();
        failureCount.set(0);
        if (currentState != State.CLOSED) {
            state.set(State.CLOSED);
            lastStateChangeTime.set(System.currentTimeMillis());
            System.out.println("[CircuitBreaker - " + serviceName + "] Service recovered! Transitioning to CLOSED.");
        }
    }

    /**
     * Records a downstream failure (e.g. timeout, connection error).
     */
    public void recordFailure() {
        int currentFailures = failureCount.incrementAndGet();
        State currentState = state.get();

        if (currentState == State.CLOSED) {
            if (currentFailures >= failureThreshold) {
                if (state.compareAndSet(State.CLOSED, State.OPEN)) {
                    lastStateChangeTime.set(System.currentTimeMillis());
                    System.err.println("[CircuitBreaker - " + serviceName + "] Failure threshold reached (" + currentFailures 
                            + "). Transitioning to OPEN. Downstream calls blocked for next " + (cooldownDurationMs / 1000) + "s.");
                }
            }
        } else if (currentState == State.HALF_OPEN) {
            // Any failure in HALF_OPEN trips it back to OPEN immediately
            if (state.compareAndSet(State.HALF_OPEN, State.OPEN)) {
                lastStateChangeTime.set(System.currentTimeMillis());
                System.err.println("[CircuitBreaker - " + serviceName + "] Call failed in HALF_OPEN. Tripping back to OPEN.");
            }
        }
    }

    public State getState() {
        return state.get();
    }
}

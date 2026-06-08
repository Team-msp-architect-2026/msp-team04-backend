package com.moment.momentbackend.global.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class BusinessMetricsService {

    private static final String UNKNOWN = "unknown";

    private final MeterRegistry meterRegistry;

    public void recordApplicationCreated() {
        afterCommit(() -> increment(
                "moment_business_application_total",
                "result", "created",
                "reason", "success"
        ));
    }

    public void recordApplicationFailed(String reason) {
        increment(
                "moment_business_application_total",
                "result", "failed",
                "reason", sanitize(reason)
        );
    }

    public void recordPaymentPrepared(String provider, String result, String reason) {
        afterCommit(() -> increment(
                "moment_business_payment_total",
                "phase", "prepare",
                "provider", sanitize(provider),
                "result", sanitize(result),
                "reason", sanitize(reason)
        ));
    }

    public void recordPaymentConfirmed(String provider) {
        afterCommit(() -> increment(
                "moment_business_payment_total",
                "phase", "confirm",
                "provider", sanitize(provider),
                "result", "confirmed",
                "reason", "success"
        ));
    }

    public void recordPaymentFailed(String phase, String provider, String reason) {
        increment(
                "moment_business_payment_total",
                "phase", sanitize(phase),
                "provider", sanitize(provider),
                "result", "failed",
                "reason", sanitize(reason)
        );
    }

    public void recordPaymentFailurePersisted(String phase, String provider, String reason) {
        afterCommit(() -> increment(
                "moment_business_payment_total",
                "phase", sanitize(phase),
                "provider", sanitize(provider),
                "result", "failed",
                "reason", sanitize(reason)
        ));
    }

    public <T> T recordSearch(String type, Supplier<T> supplier) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            T result = supplier.get();
            afterCommit(() -> increment(
                    "moment_business_search_requests_total",
                    "type", sanitize(type),
                    "result", "success"
            ));
            return result;
        } catch (RuntimeException e) {
            increment(
                    "moment_business_search_requests_total",
                    "type", sanitize(type),
                    "result", "failed"
            );
            throw e;
        } finally {
            sample.stop(Timer.builder("moment_business_search_duration_seconds")
                    .description("Business search request duration")
                    .tag("type", sanitize(type))
                    .register(meterRegistry));
        }
    }

    public <T> T recordRecommendation(String type, Supplier<T> supplier) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            T result = supplier.get();
            afterCommit(() -> increment(
                    "moment_business_recommendation_requests_total",
                    "type", sanitize(type),
                    "result", "success"
            ));
            return result;
        } catch (RuntimeException e) {
            increment(
                    "moment_business_recommendation_requests_total",
                    "type", sanitize(type),
                    "result", "failed"
            );
            throw e;
        } finally {
            sample.stop(Timer.builder("moment_business_recommendation_duration_seconds")
                    .description("Business recommendation request duration")
                    .tag("type", sanitize(type))
                    .register(meterRegistry));
        }
    }

    public void recordSearchSource(String type, String source) {
        afterCommit(() -> increment(
                "moment_business_search_source_total",
                "type", sanitize(type),
                "source", sanitize(source)
        ));
    }

    public void recordRecommendationSource(String type, String source) {
        afterCommit(() -> increment(
                "moment_business_recommendation_source_total",
                "type", sanitize(type),
                "source", sanitize(source)
        ));
    }

    public void recordAiRecommendationSource(String type, String source) {
        recordRecommendationSource(type, source);
    }

    private void increment(String metricName, String... tags) {
        meterRegistry.counter(metricName, tags).increment();
    }

    private void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
            return;
        }

        action.run();
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }

        return value.trim()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("[^A-Za-z0-9_\\-]", "_")
                .replaceAll("_+", "_")
                .toLowerCase(Locale.ROOT);
    }
}

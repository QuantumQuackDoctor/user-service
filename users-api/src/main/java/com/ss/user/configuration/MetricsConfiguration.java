package com.ss.user.configuration;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MetricsConfiguration {
    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient(@Value("${metrics.cloudwatch.region}")String region) {
        return CloudWatchAsyncClient.builder().region(Region.of(region)).build();
    }

    @Bean
    public MeterRegistry getMeterRegistry(CloudWatchConfig cloudWatchConfig, CloudWatchAsyncClient cloudWatchAsyncClient) {
        return new CloudWatchMeterRegistry(
                cloudWatchConfig,
                Clock.SYSTEM,
                cloudWatchAsyncClient
        );
    }

    @Bean
    public CloudWatchConfig setupCloudWatchConfig(@Value("${metrics.cloudwatch.namespace}")String namespace){
        return new CloudWatchConfig() {
            private final Map<String, String> configuration = new HashMap<String, String>() {{
                put("cloudwatch.namespace", namespace);
                put("cloudwatch.step", Duration.ofMinutes(1).toString());
            }};
            @Override
            public String get(String s) {
                return configuration.get(s);
            }
        };
    }
}

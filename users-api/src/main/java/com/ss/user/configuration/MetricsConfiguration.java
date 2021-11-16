package com.ss.user.configuration;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryPropertiesConfigAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
public class MetricsConfiguration {

    @Bean
    public CloudWatchMeterRegistry cloudWatchMeterRegistry(CloudWatchConfig config, Clock clock){
        return new CloudWatchMeterRegistry(config, clock, CloudWatchAsyncClient.create());
    }

    @Component
    public static class MicrometerCloudWatchConfig
            extends StepRegistryPropertiesConfigAdapter<StepRegistryProperties>
            implements CloudWatchConfig {
        private final String namespace;
        private final boolean enabled;

        public MicrometerCloudWatchConfig(@Value("${metrics.cloudwatch.namespace}") String namespace,
                                          @Value("${metrics.cloudwatch.enabled}") boolean enabled){
            super(new StepRegistryProperties() {
            });
            this.namespace = namespace;
            this.enabled = enabled;
        }

        @Override
        public boolean enabled() {
            return enabled;
        }

        @Override
        public String namespace() {
            return namespace;
        }

        @Override
        public int batchSize() {
            return CloudWatchConfig.MAX_BATCH_SIZE;
        }
    }
}

package com.yesnault.sag.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.*;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfiguration extends MetricsConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsConfiguration.class);

    public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static final HealthCheckRegistry HEALTH_CHECK_REGISTRY = new HealthCheckRegistry();

    @Inject
    private Environment env;

    @Inject
    private DataSource dataSource;

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private CacheManager cacheManager;

    @Override
    public MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }

    @Override
    public HealthCheckRegistry getHealthCheckRegistry() {
        return HEALTH_CHECK_REGISTRY;
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("Registring JVM gauges");
        METRIC_REGISTRY.register("jvm.memory", new MemoryUsageGaugeSet());
        METRIC_REGISTRY.register("jvm.garbage", new GarbageCollectorMetricSet());
        METRIC_REGISTRY.register("jvm.threads", new ThreadStatesGaugeSet());
        METRIC_REGISTRY.register("jvm.files", new FileDescriptorRatioGauge());
        METRIC_REGISTRY.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));

        /*LOGGER.debug("Initializing Metrics healthchecks");
        HEALTH_CHECK_REGISTRY.register("database", new DatabaseHealthCheck(dataSource));
        HEALTH_CHECK_REGISTRY.register("email", new JavaMailHealthCheck(javaMailSender));
        */
    }

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        LOGGER.info("Initializing Metrics JMX reporting");
        final JmxReporter jmxReporter = JmxReporter.forRegistry(METRIC_REGISTRY).build();
        jmxReporter.start();
        /*if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
            String graphiteHost = env.getProperty("metrics.graphite.host");
            if (graphiteHost != null) {
                LOGGER.info("Initializing Metrics Graphite reporting");
                Integer graphitePort = env.getProperty("metrics.graphite.port", Integer.class);
                Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
                GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(METRIC_REGISTRY)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build(graphite);
                graphiteReporter.start(1, TimeUnit.MINUTES);
            } else {
                LOGGER.warn("Graphite server is not configured, unable to send any data to Graphite");
            }
        }*/
    }
}

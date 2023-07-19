package com.example.opentelemetry.otlp.server;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class MetricsHandler extends MetricsServiceGrpc.MetricsServiceImplBase {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> responseObserver) {
        for (ResourceMetrics resourceMetrics : request.getResourceMetricsList()) {
            logger.debug("Resource: " + resourceMetrics.getResource().getAttributesList().stream().map(kv -> kv.getKey() + ":" + kv.getValue().getStringValue()).collect(Collectors.joining(",")));
            for (ScopeMetrics scopeMetrics : resourceMetrics.getScopeMetricsList()) {
                logger.debug("Scope: " + scopeMetrics.getScope().getName() + ":" + scopeMetrics.getScope().getVersion());
                for (Metric metric : scopeMetrics.getMetricsList()) {
                    logger.info("Metric: " + metric.getName() + ", unit=" + metric.getUnit());
                }
            }
        }
    }
}

package com.example.opentelemetry.otlp.server;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class TracesHandler extends TraceServiceGrpc.TraceServiceImplBase {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        logger.debug("export");
        for (ResourceSpans resourceSpans : request.getResourceSpansList()) {
            logger.debug("Resource: " + resourceSpans.getResource().getAttributesList().stream().map(kv -> kv.getKey() + ":" + kv.getValue().getStringValue()).collect(Collectors.joining(",")));
            for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
                logger.debug("Scope: " + scopeSpans.getScope().getName() + ":" + scopeSpans.getScope().getVersion());
                for (Span span : scopeSpans.getSpansList()) {
                    logger.debug("Span: " + span.getName() + ", starts=" + span.getStartTimeUnixNano());
                }
            }
        }
    }
}

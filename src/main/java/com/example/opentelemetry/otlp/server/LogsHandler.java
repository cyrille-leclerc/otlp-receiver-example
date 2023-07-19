package com.example.opentelemetry.otlp.server;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;
import io.opentelemetry.proto.collector.logs.v1.LogsServiceGrpc;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import io.opentelemetry.proto.logs.v1.ScopeLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogsHandler extends LogsServiceGrpc.LogsServiceImplBase {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void export(ExportLogsServiceRequest request, StreamObserver<ExportLogsServiceResponse> responseObserver) {
        logger.debug("export");
        for(ResourceLogs resourceLogs: request.getResourceLogsList()) {
            for(ScopeLogs scopeLogs: resourceLogs.getScopeLogsList()) {
                for(LogRecord logRecord: scopeLogs.getLogRecordsList()) {
                    logger.debug(logRecord.getBody().getStringValue());
                }
            }
        }
    }
}

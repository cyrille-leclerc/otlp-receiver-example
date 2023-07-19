package com.example.opentelemetry.otlp.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OtlpServer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private Server server;
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        // See https://github.com/opensearch-project/data-prepper/blob/main/data-prepper-plugins/otel-trace-source/src/main/java/org/opensearch/dataprepper/plugins/source/oteltrace/OTelTraceSource.java#L104

        OtlpServer otlpServer = new OtlpServer();
        otlpServer.start();
        otlpServer.blockUntilShutdown();

    }

    public void start() throws IOException {
        TracesHandler oTelTraceGrpcService = new TracesHandler();
        LogsHandler logsHandler = new LogsHandler();

        this.server = ServerBuilder
                .forPort(4316)
                .addService(oTelTraceGrpcService)
                .addService(logsHandler)
                .build();
        server.start();

        logger.debug("OTLP Server started on port: " + server.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            logger.debug("*** shutting down gRPC server since JVM is shutting down");
            try {
                stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            logger.info("*** server shut down");
        }));

    }
    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread as the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}

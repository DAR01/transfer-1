package com.dzavorin.transfer.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.github.susom.database.DatabaseProviderVertx.*;
import static com.dzavorin.transfer.utils.Constants.*;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.*;

/**
 * Abstract service, basically encapsulates database builder
 * @param <T> type parameter for entities to work with
 */
abstract class BaseService<T> {

    protected final Builder databaseBuilder;

    protected BaseService(Builder databaseBuilder) {
        this.databaseBuilder = databaseBuilder;
    }

    /**
     * Creates basic request async result handler.
     * Adds common headers to response.
     * Encodes response body to JSON.
     * @param context RoutingContext
     */
    protected Handler<AsyncResult<T>> getResultHandler(final RoutingContext context) {
        return call -> {
            if (call.succeeded()) {
                if (call.result() == null) {
                    context.fail(BAD_REQUEST);
                } else {
                    context.response()
                        .putHeader(SERVER, SERVER_NAME)
                        .putHeader(DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()))
                        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .end(Json.encodeToBuffer(call.result()));
                }
            } else {
                context.fail(call.cause());
            }
        };
    }
}

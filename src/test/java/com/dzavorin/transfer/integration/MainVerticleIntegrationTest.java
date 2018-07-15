package com.dzavorin.transfer.integration;

import com.dzavorin.transfer.MainVerticle;
import com.dzavorin.transfer.model.Account;
import com.dzavorin.transfer.model.AccountTransferPair;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import java.math.BigDecimal;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.*;
import static io.vertx.core.http.HttpMethod.PATCH;
import static io.vertx.core.json.Json.*;
import static com.dzavorin.transfer.utils.Constants.*;

/**
 * Test cases are using same MainVerticle as real application.
 * Database is initialized with two demo accounts:
 *     {
 *         "id": 1,
 *         "amount": 10
 *     },
 *     {
 *         "id": 2,
 *         "amount": 10
 *     }
 */
@RunWith(VertxUnitRunner.class)
public class MainVerticleIntegrationTest {

    private Vertx vertx;
    private Integer port;
    private HttpClient client;

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        port = new JsonObject(vertx.fileSystem().readFileBlocking(PATH_TO_CONFIG)).getInteger(PORT);
        vertx.deployVerticle(MainVerticle.class.getName(), context.asyncAssertSuccess());
        client = vertx.createHttpClient();
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void createAccountOk(TestContext context) {
        Async async = context.async();

        String requestBody = "{\"amount\": 35}";
        String expected = encode(new Account(3L, new BigDecimal(35)));

        client.post(port, LOCALHOST, "/account")
            .putHeader(CONTENT_LENGTH, Integer.toString(requestBody.length()))
            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .handler(response -> {
                context.assertEquals(OK, response.statusCode());
                response.bodyHandler(body -> context.assertEquals(expected, body.toString(UTF8)));
                client.close();
                async.complete();
            })
            .write(requestBody)
            .end();
    }

    @Test
    public void createAccountFail(TestContext context) {
        Async async = context.async();

        String requestBody = "{\"amount\": abc}";

        client.post(port, LOCALHOST, "/account")
            .putHeader(CONTENT_LENGTH, Integer.toString(requestBody.length()))
            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .handler(response -> {
                context.assertEquals(BAD_REQUEST, response.statusCode());
                async.complete();
                client.close();
            })
            .write(requestBody)
            .end();
    }

    @Test
    public void getAccountOk(TestContext context) {
        Async async = context.async();

        String expected = encode(new Account(2L, new BigDecimal(10)));
        client.getNow(port, LOCALHOST, "/account/2", response -> {
            response.bodyHandler(body ->
                context.assertEquals(expected, body.toString(UTF8)));
            client.close();
            async.complete();
        });
    }

    @Test
    public void getAccountFail(TestContext context) {
        Async async = context.async();

        client.getNow(port, LOCALHOST, "/account/0", response -> {
            context.assertEquals(BAD_REQUEST, response.statusCode());
            client.close();
            async.complete();
        });
    }

    @Test
    public void makeTransferOk(TestContext context) {
        Async async = context.async();

        AccountTransferPair transferPair = new AccountTransferPair();
        transferPair.setFrom(new Account(2L, new BigDecimal(5)));
        transferPair.setTo(new Account(1L, new BigDecimal(15)));
        String expected = encode(transferPair);

        client.request(PATCH, port, LOCALHOST, "/transfer/2/1/5", response -> {
            context.assertEquals(OK, response.statusCode());
            response.bodyHandler(body -> context.assertEquals(expected, body.toString(UTF8)));
            client.close();
            async.complete();
        }).end();
    }

    @Test
    public void makeTransferInsufficientFundsFail(TestContext context) {
        Async async = context.async();

        client.request(PATCH, port, LOCALHOST, "/transfer/2/1/50", response -> {
            context.assertEquals(BAD_REQUEST, response.statusCode());
            client.close();
            async.complete();
        }).end();
    }

    @Test
    public void makeTransferNoAccountFail(TestContext context) {
        Async async = context.async();

        client.request(PATCH, port, LOCALHOST, "/transfer/2/0/50", response -> {
            context.assertEquals(BAD_REQUEST, response.statusCode());
            client.close();
            async.complete();
        }).end();
    }

}

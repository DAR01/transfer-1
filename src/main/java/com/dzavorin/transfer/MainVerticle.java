package com.dzavorin.transfer;

import com.dzavorin.transfer.config.DBConfig;
import com.dzavorin.transfer.service.AccountService;
import com.dzavorin.transfer.service.TransferService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dzavorin.transfer.utils.Constants.BAD_REQUEST;
import static com.dzavorin.transfer.utils.Constants.PATH_TO_CONFIG;
import static com.dzavorin.transfer.utils.Constants.PORT;

/**
 * Main application entry point.
 */
public class MainVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    private AccountService accountService;
    private TransferService transferService;
    private JsonObject config;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        // Read config file
        config = new JsonObject(vertx.fileSystem().readFileBlocking(PATH_TO_CONFIG));

        // Initialise database
        DBConfig dbConfig = new DBConfig(vertx, config);

        // Initialise services
        accountService = new AccountService(dbConfig.getBuilder());
        transferService = new TransferService(dbConfig.getBuilder());
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        // Configuration for handling requests body payload and basic error handling for all routes
        router.route().handler(BodyHandler.create())
            .failureHandler(failureCtx -> failureCtx.response().setStatusCode(BAD_REQUEST).end());

        // Define routes and their handlers
        router.get("/account/:id").handler(accountService::getAccountHandler);
        router.post("/account").handler(accountService::createAccountHandler);
        router.patch("/transfer/:fromId/:toId/:amount").handler(transferService::transferHandler);

        // Start http server
        vertx.createHttpServer().requestHandler(router::accept).listen(config.getInteger(PORT));
    }

    /**
     * Runner for IDE
     */
    public static void main(final String[] args) {
        Launcher.executeCommand("run", MainVerticle.class.getName());
    }
}

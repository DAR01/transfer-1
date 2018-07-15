package com.dzavorin.transfer.service;

import com.dzavorin.transfer.model.Account;
import com.dzavorin.transfer.repository.AccountDao;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.dzavorin.transfer.utils.Constants.AMOUNT;
import static com.dzavorin.transfer.utils.Constants.ID;
import static com.github.susom.database.DatabaseProviderVertx.*;

/**
 * Service for operations with single account
 */
public class AccountService extends BaseService<Account> {

    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public AccountService(Builder databaseBuilder) {
        super(databaseBuilder);
    }

    /**
     * Handler for create account request
     */
    public void createAccountHandler(final RoutingContext context) {
        BigDecimal amount = new BigDecimal(context.getBodyAsJson().getDouble(AMOUNT));
        logger.info("Request to create new account with amount {}", amount);
        databaseBuilder.transactAsync(dbs -> new AccountDao(dbs).create(amount), getResultHandler(context));
    }

    /**
     * Handler for retrieve account by id request
     */
    public void getAccountHandler(final RoutingContext context) {
        Long id = Long.valueOf(context.request().getParam(ID));
        logger.info("Get account id{} request", id);
        databaseBuilder.transactAsync(dbs -> new AccountDao(dbs).getOne(id), getResultHandler(context));
    }

}

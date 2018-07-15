package com.dzavorin.transfer.service;

import com.dzavorin.transfer.model.AccountTransferPair;
import com.dzavorin.transfer.repository.TransferDao;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.dzavorin.transfer.utils.Constants.*;
import static com.github.susom.database.DatabaseProviderVertx.*;
import static java.lang.Long.*;

/**
 * Service for money transfer operations between accounts
 */
public class TransferService extends BaseService<AccountTransferPair>{

    private final Logger logger = LoggerFactory.getLogger(TransferService.class);

    public TransferService(Builder databaseBuilder) {
        super(databaseBuilder);
    }

    /**
     * Handler for transfer money request
     */
    public void transferHandler(final RoutingContext context) {
        Long fromId = valueOf(context.request().getParam(FROM_ID));
        Long toId = valueOf(context.request().getParam(TO_ID));
        BigDecimal amount = new BigDecimal(context.request().getParam(AMOUNT).replaceAll(",", ""));
        logger.info("Money transfer request from id{} to id{} for amount {}", fromId, toId, amount);
        databaseBuilder.transactAsync(dbs -> new TransferDao(dbs).transfer(fromId, toId, amount), getResultHandler(context));
    }
}

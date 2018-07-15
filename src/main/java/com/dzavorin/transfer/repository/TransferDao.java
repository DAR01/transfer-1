package com.dzavorin.transfer.repository;

import com.dzavorin.transfer.exceptions.ImpossibleOperationException;
import com.dzavorin.transfer.exceptions.InsufficientFundsException;
import com.dzavorin.transfer.model.Account;
import com.dzavorin.transfer.model.AccountTransferPair;
import com.github.susom.database.Database;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import static com.dzavorin.transfer.utils.Constants.*;
import static java.math.BigDecimal.ZERO;

/**
 * Data access wrapper for money transfer operations
 */
public class TransferDao extends AbstractDao {

    private static final String SELECT_AMOUNT_BY_ID = "SELECT id, amount FROM accounts WHERE id IN(:fromId, :toId)";
    private static final String UPDATE_AMOUNT_BY_IDS = "UPDATE accounts SET amount = CASE id " +
        "WHEN :fromId THEN :subtracted WHEN :toId THEN :added ELSE amount END WHERE id IN(:fromId, :toId)";

    public TransferDao(Supplier<Database> dbs) {
        super(dbs);
    }

    /**
     * Retrieves accounts for transfer from database, checks whether transfer is applicable,
     * updates accounts.
     *
     * @param fromId id of account to retrieve money from
     * @param toId   id of account to receive money to
     * @param amount the amount of passed money
     * @return AccountTransferPair representing accounts state after transfer
     */
    public AccountTransferPair transfer(Long fromId, Long toId, BigDecimal amount) {
        List<Account> accounts = databaseSupplier.get().toSelect(SELECT_AMOUNT_BY_ID)
            .argLong(FROM_ID, fromId)
            .argLong(TO_ID, toId)
            .queryMany(row -> new Account(row.getLongOrNull(), row.getBigDecimalOrNull()));

        AccountTransferPair transferPair = validateQueryResult(accounts, fromId, toId, amount);

        databaseSupplier.get().toUpdate(UPDATE_AMOUNT_BY_IDS)
            .argLong(FROM_ID, fromId)
            .argLong(TO_ID, toId)
            .argBigDecimal(SUBTRACTED, transferPair.getFrom().getAmount())
            .argBigDecimal(ADDED, transferPair.getTo().getAmount()).update(2);

        return transferPair;
    }

    /**
     * Validates if transfer is applicable
     */
    private AccountTransferPair validateQueryResult(List<Account> accounts, Long fromId, Long toId, BigDecimal amount) {
        if (accounts.size() != 2 && !accounts.get(0).notEmpty() && !accounts.get(1).notEmpty()) {
            throw new ImpossibleOperationException();
        }
        AccountTransferPair transferPair = new AccountTransferPair();
        for (Account account : accounts) {
            if (account.getId().equals(fromId)) {
                BigDecimal subtracted = account.getAmount().subtract(amount);
                if (subtracted.compareTo(ZERO) < 0) {
                    throw new InsufficientFundsException();
                }
                account.setAmount(subtracted);
                transferPair.setFrom(account);
            } else if (account.getId().equals(toId)) {
                account.setAmount(account.getAmount().add(amount));
                transferPair.setTo(account);
            } else {
                throw new ImpossibleOperationException();
            }
        }
        return transferPair;
    }
}

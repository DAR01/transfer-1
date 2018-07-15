package com.dzavorin.transfer.repository;

import com.dzavorin.transfer.model.Account;
import com.github.susom.database.Database;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static com.dzavorin.transfer.utils.Constants.*;

/**
 * Account data access wrapper
 */
public class AccountDao extends AbstractDao {

    private static final String INSERT_ACCOUNT = "INSERT INTO accounts (id, amount) VALUES (?, ?)";
    private static final String SELECT_BY_ID = "SELECT id, amount FROM accounts WHERE id=?";

    public AccountDao(Supplier<Database> dbs) {
        super(dbs);
    }

    /**
     * Creates new account in DB
     * @param amount represents starting balance of a new account
     * @return newly created account representation including unique id
     */
    public Account create(final BigDecimal amount) {
        Long id = databaseSupplier.get().toInsert(INSERT_ACCOUNT)
            .argPkSeq(ID_SEQUENCE)
            .argBigDecimal(amount)
            .insertReturningPkSeq(ID);
        return new Account(id, amount);
    }

    /**
     * Retrieves account info from DB by unique id
     * @param id unique identifier of account
     * @return account representation from DB
     */
    public Account getOne(final Long id) {
        return databaseSupplier.get().toSelect(SELECT_BY_ID).argLong(id)
            .queryMany(row -> new Account(row.getLongOrNull(), row.getBigDecimalOrNull())).get(0);
    }
}

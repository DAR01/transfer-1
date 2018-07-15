package com.dzavorin.transfer.repository;

import com.github.susom.database.Database;

import java.util.function.Supplier;

/**
 * Abstract data access object that represents a wrapper for holding passed database supplier,
 * Generally used to handle database queries.
 */
abstract class AbstractDao {

    protected final Supplier<Database> databaseSupplier;

    protected AbstractDao(Supplier<Database> databaseSupplier) {
        this.databaseSupplier = databaseSupplier;
    }
}

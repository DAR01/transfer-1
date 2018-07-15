package com.dzavorin.transfer.config;

import com.github.susom.database.Config;
import com.github.susom.database.ConfigFrom;
import com.github.susom.database.DatabaseProviderVertx;
import com.github.susom.database.DatabaseProviderVertx.Builder;
import com.github.susom.database.Schema;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.dzavorin.transfer.utils.Constants.*;

/**
 * Basic class for instantiating database by passed config and working verticle
 */
public class DBConfig {

    private static final String INSERT_DEMO_ACCOUNTS = "INSERT INTO accounts (id, amount) values (?,?)";

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final Builder databaseBuilder;

    public DBConfig(Vertx vertx, JsonObject jsonConfig) {
        Config config = ConfigFrom.firstOf().custom(jsonConfig::getString).get();

        // Creates database builder for work with database
        databaseBuilder = DatabaseProviderVertx.pooledBuilder(vertx, config)
            .withSqlInExceptionMessages()
            .withSqlParameterLogging();

        // Set up a table
        vertx.executeBlocking(call ->
            databaseBuilder.transact(db -> {
                db.get().dropTableQuietly(ACCOUNTS_TABLE);
                db.get().dropSequenceQuietly(ID_SEQUENCE);

                new Schema().addTable(ACCOUNTS_TABLE)
                    .addColumn(ID).primaryKey().table()
                    .addColumn(AMOUNT).asBigDecimal(8, 1).schema()
                    .addSequence(ID_SEQUENCE).schema().execute(db);

                // Upload two demo accounts
                db.get().toInsert(INSERT_DEMO_ACCOUNTS)
                    .argPkSeq(ID_SEQUENCE)
                    .argBigDecimal(new BigDecimal(10)).batch()
                    .argPkSeq(ID_SEQUENCE).argBigDecimal(new BigDecimal(10))
                    .insertBatch();
            }), res -> {
            if (res.succeeded()) {
                logger.info("Database successfully configured");
            } else {
                logger.warn(res.cause().getMessage());
            }
        });
    }

    /**
     * @return newly created database builder
     */
    public Builder getBuilder() {
        return databaseBuilder;
    }
}

package com.craftaro.ultimatefishing.database.migrations;

import com.craftaro.core.database.DataMigration;
import com.craftaro.core.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_InitialMigration extends DataMigration {

    public _1_InitialMigration() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector databaseConnector, String tablePrefix) throws SQLException {
        // Create caught table
        try (Statement statement = databaseConnector.getConnection().createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "caught (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "rarity TEXT NOT NULL," +
                    "amount INT NOT NULL " +
                    ")");

        }
    }

}

package com.pinewoodbuilders.database.migrate.migrations;

import com.pinewoodbuilders.Constants;
import com.pinewoodbuilders.contracts.database.migrations.Migration;
import com.pinewoodbuilders.database.schema.Schema;

import java.sql.SQLException;

public class CreateEvaluationSettingsTableMigration implements Migration {
  
    @Override
    public String created_at() {
        return "Sat, Sep 25, 2021 8:41 PM";
    }

    @Override
    public boolean up(Schema schema) throws SQLException {
        return schema.createIfNotExists(Constants.GROUP_SETTINGS_TABLE, table -> {
            table.Long("group_id");
            table.String("name");
            table.String("evaluation_settings").defaultValue("base64:eyJyYW5rU2V0dGluZ3MiOiBbXX0=");
        });
    }    

    @Override
    public boolean down(Schema schema) throws SQLException {
        return schema.dropIfExists(Constants.GROUP_SETTINGS_TABLE);
    }  
}

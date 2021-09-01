package com.pinewoodbuilders.database.migrate.migrations;

import java.sql.SQLException;

import com.pinewoodbuilders.Constants;
import com.pinewoodbuilders.contracts.database.migrations.Migration;
import com.pinewoodbuilders.database.schema.Schema;

public class CreateGuildSettingsTableMigration implements Migration {
  
    @Override
    public String created_at() {
        return "Sat, Aug 28, 2021 10:29 PM";
    }

    @Override
    public boolean up(Schema schema) throws SQLException {
        return schema.createIfNotExists(Constants.GUILD_SETTINGS_TABLE, table -> {
            table.Increments("id");
            table.Long("roblox_group_id");
            table.String("group_name");
            table.Long("main_group_id");
            table.Long("main_discord_role");
            table.Integer("minimum_hr_rank");
            table.Integer("minimum_lead_rank");
            table.Boolean("global_ban").defaultValue(false);
            table.Boolean("global_kick").defaultValue(false);
            table.Boolean("global_verify").defaultValue(false);
            table.Boolean("global_anti_unban").defaultValue(false);
            table.Boolean("global_filter").defaultValue(false);
            table.LongText("global_filter_exact").nullable();
            table.LongText("global_filter_wildcard").nullable();
            table.Long("global_filter_log_channel").nullable();
            table.Boolean("global_automod").nullable();
            table.Integer("automod_mass_mention", 11).defaultValue(15);
            table.Integer("automod_emoji_spam", 11).defaultValue(15);
            table.Integer("automod_link_spam", 11).defaultValue(15);
            table.Integer("automod_message_spam", 11).defaultValue(15);
            table.Integer("automod_image_spam", 11).defaultValue(15);
            table.Integer("automod_character_spam", 11).defaultValue(15);
            table.LongText("admin_roles").nullable();
            table.LongText("manager_roles").nullable();
            table.LongText("moderator_roles").nullable();
            table.LongText("group_shout_roles").nullable();
            table.LongText("no_links_roles").nullable();
            table.Boolean("pb_verification_trelloban").nullable();
            table.String("pb_verification_blacklist_link").nullable();
            table.Boolean("verification_anti_main_global_mod_impersonate").nullable();
            table.Boolean("permission_bypass").defaultValue(false);
        
            table.Long("emoji_id").nullable();
            table.Long("on_watch_channel").nullable();
            table.Long("on_watch_role").nullable();
            table.Boolean("local_filter").defaultValue(false);
            table.Long("local_filter_log").nullable();
            table.LongText("filter_exact").nullable();
            table.LongText("filter_wildcard").nullable();
            table.Long("patrol_remittance_channel").nullable();
            table.LongText("patrol_remittance_message").nullable();
            table.Long("handbook_report_channel").nullable();
            table.Long("suggestion_channel_id").nullable();
            table.Long("suggestion_community_channel_id").nullable();
            table.Long("suggestion_approved_channel_id").nullable();
            table.Long("join_logs").nullable();
            table.Long("audit_logs_channel_id").nullable();
            table.Long("vote_validation_channel_id").nullable();
            table.Long("user_alerts_channel_id").nullable();
            table.Long("evaluation_answer_channel").nullable();
            table.LongText("eval_questions").nullable();
        });
    }    

    @Override
    public boolean down(Schema schema) throws SQLException {
        return schema.dropIfExists(Constants.GUILD_SETTINGS_TABLE);
    }  
}

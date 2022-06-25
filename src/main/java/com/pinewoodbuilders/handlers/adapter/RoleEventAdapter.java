/*
 * Copyright (c) 2018.
 *
 * This file is part of Xeus.
 *
 * Xeus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Xeus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Xeus.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package com.pinewoodbuilders.handlers.adapter;

import com.pinewoodbuilders.Xeus;
import com.pinewoodbuilders.Constants;
import com.pinewoodbuilders.contracts.handlers.EventAdapter;
import com.pinewoodbuilders.database.controllers.GuildController;
import com.pinewoodbuilders.database.transformers.GuildTransformer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;

import java.sql.SQLException;
import java.util.Map;

public class RoleEventAdapter extends EventAdapter {

    /**
     * Instantiates the event adapter and sets the avaire class instance.
     *
     * @param avaire The Xeus application class instance.
     */
    public RoleEventAdapter(Xeus avaire) {
        super(avaire);
    }

    public void onRoleUpdateName(RoleUpdateNameEvent event) {
        GuildTransformer transformer = GuildController.fetchGuild(avaire, event.getGuild());
        if (transformer == null || transformer.getSelfAssignableRoles().isEmpty()) {
            return;
        }

        if (!transformer.getSelfAssignableRoles().containsKey(event.getRole().getId())) {
            return;
        }

        try {
            transformer.getSelfAssignableRoles().put(event.getRole().getId(), event.getRole().getName().toLowerCase());
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_TABLE_NAME)
                .useAsync(true)
                .where("id", event.getGuild().getId())
                .update(statement -> {
                    statement.set("claimable_roles", Xeus.gson.toJson(transformer.getSelfAssignableRoles()), true);
                });
        } catch (SQLException e) {
            Xeus.getLogger().error("ERROR: ", e);
        }
    }

    
    public void onRoleDelete(RoleDeleteEvent event) {
        GuildTransformer transformer = GuildController.fetchGuild(avaire, event.getGuild());
        if (transformer == null) {
            return;
        }

        handleMuteRole(event, transformer);
        handleAutoroles(event, transformer);
        handleLevelRoles(event, transformer);
        handleSelfAssignableRoles(event, transformer);
    }

    private void handleMuteRole(RoleDeleteEvent event, GuildTransformer transformer) {
        if (transformer.getMuteRole() == null || !event.getRole().getId().equals(transformer.getMuteRole())) {
            return;
        }

        try {
            transformer.setMuteRole(null);
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_TABLE_NAME)
                .useAsync(true)
                .where("id", event.getGuild().getId())
                .update(statement -> statement.set("mute_role", null));
        } catch (SQLException e) {
            Xeus.getLogger().error("ERROR: ", e);
        }
    }

    private void handleAutoroles(RoleDeleteEvent event, GuildTransformer transformer) {
        if (transformer.getAutorole() == null || !event.getRole().getId().equals(transformer.getAutorole())) {
            return;
        }

        try {
            transformer.setAutorole(null);
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_TABLE_NAME)
                .useAsync(true)
                .where("id", event.getGuild().getId())
                .update(statement -> statement.set("autorole", null));
        } catch (SQLException e) {
            Xeus.getLogger().error("ERROR: ", e);
        }
    }

    private void handleLevelRoles(RoleDeleteEvent event, GuildTransformer transformer) {
        if (transformer.getLevelRoles().isEmpty() || !transformer.getLevelRoles().containsValue(event.getRole().getId())) {
            return;
        }

        int key = -1;
        for (Map.Entry<Integer, String> entry : transformer.getLevelRoles().entrySet()) {
            if (entry.getValue().equals(event.getRole().getId())) {
                key = entry.getKey();
            }
        }

        try {
            transformer.getLevelRoles().remove(key);
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_TABLE_NAME)
                .useAsync(true)
                .where("id", event.getGuild().getId())
                .update(statement -> {
                    statement.set("level_roles", Xeus.gson.toJson(transformer.getLevelRoles()), true);
                });
        } catch (SQLException e) {
            Xeus.getLogger().error("ERROR: ", e);
        }
    }

    private void handleSelfAssignableRoles(RoleDeleteEvent event, GuildTransformer transformer) {
        if (transformer.getSelfAssignableRoles().isEmpty() || !transformer.getSelfAssignableRoles().containsKey(event.getRole().getId())) {
            return;
        }

        try {
            transformer.getSelfAssignableRoles().remove(event.getRole().getId());
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_TABLE_NAME)
                .useAsync(true)
                .where("id", event.getGuild().getId())
                .update(statement -> {
                    statement.set("claimable_roles", Xeus.gson.toJson(transformer.getSelfAssignableRoles()), true);
                });
        } catch (SQLException e) {
            Xeus.getLogger().error("ERROR: ", e);
        }
    }


    public void updateRoleData(Guild guild) {
        try {
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_TABLE_NAME)
                .useAsync(true)
                .where("id", guild.getId())
                .update(statement -> {
                    statement.set("roles_data", GuildController.buildRoleData(guild.getRoles()), true);
                });
        } catch (SQLException e) {
            Xeus.getLogger().error("ERROR: ", e);
        }
    }
}

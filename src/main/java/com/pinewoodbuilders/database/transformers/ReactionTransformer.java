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

package com.pinewoodbuilders.database.transformers;

import com.pinewoodbuilders.Xeus;
import com.pinewoodbuilders.contracts.database.transformers.Transformer;
import com.pinewoodbuilders.database.collection.DataRow;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ReactionTransformer extends Transformer {

    private final Map<Long, Long> roles = new HashMap<>();

    private long guildId = -1;
    private long channelId = -1;
    private long messageId = -1;

    /**
     * Creates a reaction message transformer
     * for the given data row instance.
     *
     * @param row The data row that should be used to construct the reaction transformer.
     */
    public ReactionTransformer(DataRow row) {
        super(row);

        if (row != null) {
            guildId = row.getLong("guild_id");
            channelId = row.getLong("channel_id");
            messageId = row.getLong("message_id");

            if (data.getString("roles", null) != null) {
                HashMap<Long, Long> dbRoles = Xeus.gson.fromJson(
                    data.getString("roles"),
                    new TypeToken<HashMap<Long, Long>>() {
                    }.getType());

                roles.putAll(dbRoles);
            }
        }
    }

    /**
     * Gets the role ID that is attached to the given emote, if
     * no role is linked with the given emote then
     * {@code NULL} will be returned instead.
     *
     * @param emote The emote that should be used to get the role id.
     * @return Possibly-null, the ID of the role that is linked/attached to the given emote.
     */
    @Nullable
    public Long getRoleIdFromEmote(@Nonnull RichCustomEmoji emote) {
        if (!roles.containsKey(emote.getIdLong())) {
            return null;
        }
        return roles.get(emote.getIdLong());
    }

    /**
     * Adds the reaction to the current reaction message, linking the
     * given emote and role so when a user reactions to the message
     * with the given emote, they will be giving the role.
     *
     * @param emote The emote that should be added to the reaction message.
     * @param role  The role that should be linked to the given emote.
     */
    public void addReaction(@Nonnull RichCustomEmoji emote, @Nonnull Role role) {
        roles.put(emote.getIdLong(), role.getIdLong());
    }

    /**
     * Removes the given emote from the reaction message.
     *
     * @param emote The emote that should be removed from the reaction message.
     * @return {@code True} if the emote was removed from the message, or {@code False} if it wasn't added in the first place.
     */
    public boolean removeReaction(@Nonnull RichCustomEmoji emote) {
        if (roles.containsKey(emote.getIdLong())) {
            roles.remove(emote.getIdLong());
            return true;
        }
        return false;
    }

    /**
     * Gets a map of the roles attached to the message, where the
     * key is the ID of the emote, and the value is the role ID.
     *
     * @return A map of the roles attached to the reaction message.
     */
    public Map<Long, Long> getRoles() {
        return roles;
    }

    /**
     * Gets the ID of the guild the reaction message belongs to.
     *
     * @return The ID of the guild the reaction message belongs to.
     */
    public long getGuildId() {
        return guildId;
    }

    /**
     * Gets the ID of the channel the reaction message belongs to.
     *
     * @return The ID of the channel the reaction message belongs to.
     */
    public long getChannelId() {
        return channelId;
    }

    /**
     * Gets the ID of the message the reaction message belongs to.
     *
     * @return The ID of the message the reaction message belongs to.
     */
    public long getMessageId() {
        return messageId;
    }
}

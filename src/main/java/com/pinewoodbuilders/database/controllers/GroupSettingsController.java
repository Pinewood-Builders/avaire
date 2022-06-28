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

package com.pinewoodbuilders.database.controllers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pinewoodbuilders.Constants;
import com.pinewoodbuilders.Xeus;
import com.pinewoodbuilders.database.collection.Collection;
import com.pinewoodbuilders.database.transformers.GlobalSettingsTransformer;
import com.pinewoodbuilders.database.transformers.GuildSettingsTransformer;
import com.pinewoodbuilders.utilities.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import java.util.concurrent.TimeUnit;

public class GroupSettingsController {

    public static final Cache<Long, GlobalSettingsTransformer> cache = CacheBuilder.newBuilder().recordStats()
            .expireAfterAccess(5, TimeUnit.MINUTES).build();

    private static final Logger log = LoggerFactory.getLogger(GroupSettingsController.class);

    private static final String[] requiredSettingsColumns = new String[] {
            "group_settings.group_id", "group_settings.evaluation_settings", "group_settings.name"
    };

    /**
     * Fetches the guild transformer from the cache, if it doesn't exist in the
     * cache it will be loaded into the cache and then returned afterwords.
     *
     * @param avaire The avaire instance, used to talking to the database.
     * @return Possibly null, the guild transformer instance for the current guild,
     *         or null.
     */
    @CheckReturnValue
    public static GlobalSettingsTransformer fetchGlobalSettingsFromGroupSettings(Xeus avaire, GuildSettingsTransformer transformer) {
        if (transformer == null) {return null;}
        if (transformer.getMainGroupId() == 0) {return null;}

        return fetchGlobalSettingsFromGroupId(avaire, transformer.getRobloxGroupId());
    }

    @CheckReturnValue
    public static GlobalSettingsTransformer fetchGlobalSettingsFromGroupId(Xeus avaire, long groupId) {
        return (GlobalSettingsTransformer) CacheUtil.getUncheckedUnwrapped(cache, groupId,
                () -> loadGuildSettingsFromDatabase(avaire, groupId));
    }

    public static void forgetCache(long groupId) {
        cache.invalidate(groupId);
    }

    private static GlobalSettingsTransformer loadGuildSettingsFromDatabase(Xeus avaire, Long groupId) {
        if (log.isDebugEnabled()) {
            log.debug("Settings cache for " + groupId + " was refreshed");
        }
        try {
            Collection query = avaire.getDatabase().newQueryBuilder(Constants.GLOBAL_SETTINGS_TABLE)
                .select(requiredSettingsColumns)
                .where("global_settings.main_group_id", groupId)
                .get();

            GlobalSettingsTransformer transformer = new GlobalSettingsTransformer(query.first());

            if (query.size() == 0) {
                return null;
            } 

            return transformer;
        } catch (Exception ex) {
            log.error("Failed to fetch guild transformer from the database, error: {}", ex.getMessage(), ex);
            return null;
        }
    }
}

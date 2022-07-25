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
import com.pinewoodbuilders.contracts.roblox.evaluations.EvaluationSettings;
import com.pinewoodbuilders.database.collection.DataRow;
import com.pinewoodbuilders.database.controllers.GroupSettingsController;

public class GroupSettingsTransformer extends Transformer {

    // Global Settings
    private long groupId = 0;
    private String groupName;

    private EvaluationSettings evaluationSettings = null;

    public GroupSettingsTransformer(DataRow data) {
        super(data);
        if (hasData()) {

            groupId = data.getLong("group_id");
            groupName = data.getString("group_name");

            evaluationSettings = Xeus.gson.fromJson(data.getString("evaluation_settings"), EvaluationSettings.class);
            reset();
        }
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public EvaluationSettings getEvaluationSettings() {
        return evaluationSettings;
    }

    public void setEvaluationSettings(EvaluationSettings evaluationSettings) {
        this.evaluationSettings = evaluationSettings;
    }
}

package com.pinewoodbuilders.contracts.roblox.evaluations;

import com.google.gson.annotations.SerializedName;
import com.pinewoodbuilders.contracts.roblox.evaluations.settings.RankSetting;

import java.util.List;

public class EvaluationSettings {

    public EvaluationSettings(RankSetting[] rankSettings) {
        this.rankSettings = rankSettings;
    }

    private RankSetting[] rankSettings;

    @SerializedName("rankSettings")
    public RankSetting[] getRankSettings() { return rankSettings; }
    @SerializedName("rankSettings")
    public void setRankSettings(RankSetting[] value) { this.rankSettings = value; }
}

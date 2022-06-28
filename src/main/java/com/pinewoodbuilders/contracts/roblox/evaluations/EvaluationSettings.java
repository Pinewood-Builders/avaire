package com.pinewoodbuilders.contracts.roblox.evaluations;

import com.google.gson.annotations.SerializedName;
import com.pinewoodbuilders.contracts.roblox.evaluations.settings.RankSetting;

import java.util.List;

public class EvaluationSettings {
    private List<RankSetting> rankSettings;

    @SerializedName("rankSettings")
    public List <RankSetting> getRankSettings() { return rankSettings; }
    @SerializedName("rankSettings")
    public void setRankSettings(List<RankSetting> value) { this.rankSettings = value; }
}

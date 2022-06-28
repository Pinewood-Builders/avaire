package com.pinewoodbuilders.contracts.roblox.evaluations.settings;

import com.google.gson.annotations.SerializedName;
import com.pinewoodbuilders.contracts.roblox.evaluations.eval.Evaluation;

import java.util.List;

public class RankSetting {
    private String id;
    private List<String> aliases;
    private String description;
    private List<Evaluation> evaluations;

    @SerializedName("id")
    public String getID() { return id; }
    @SerializedName("id")
    public void setID(String value) { this.id = value; }

    @SerializedName("aliases")
    public List<String> getAliases() { return aliases; }
    @SerializedName("aliases")
    public void setAliases(List<String> value) { this.aliases = value; }

    @SerializedName("description")
    public String getDescription() { return description; }
    @SerializedName("description")
    public void setDescription(String value) { this.description = value; }

    @SerializedName("evaluations")
    public List<Evaluation> getEvaluations() { return evaluations; }
    @SerializedName("evaluations")
    public void setEvaluations(List<Evaluation> value) { this.evaluations = value; }
}


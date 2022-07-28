package com.pinewoodbuilders.contracts.roblox.evaluations.settings;

import com.google.gson.annotations.SerializedName;
import com.pinewoodbuilders.contracts.roblox.evaluations.eval.Evaluation;

import java.util.List;

public class RankSetting {
    public RankSetting(long order, String id, String name, List<String> aliases, String description, List<Evaluation> evaluations) {
        this.order = order;
        this.id = id;
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.evaluations = evaluations;
    }

    public RankSetting() {}



    private long order;
    private String id;
    private String name;
    private List<String> aliases;
    private String description;
    private List<Evaluation> evaluations;

    @SerializedName("order")
    public long getOrder() { return order; }
    @SerializedName("order")
    public void setOrder(long value) { this.order = value; }

    @SerializedName("id")
    public String getId() { return id; }
    @SerializedName("id")
    public void setId(String value) { this.id = value; }

    @SerializedName("name")
    public String getName() {
        return name;
    }

    @SerializedName("name")
    public void setName(String name) {
        this.name = name;
    }

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


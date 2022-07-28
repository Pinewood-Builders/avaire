package com.pinewoodbuilders.contracts.roblox.evaluations.eval;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Evaluation {

    public Evaluation(long order, String id, String name, String description, String[] aliases, String[] questions) {
        this.order = order;
        this.id = id;
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.questions = questions;
    }

    private long order;
    private String id;
    private String name;
    private String description;
    private String[] aliases;
    private String[] questions;

    @SerializedName("order")
    public long getOrder() { return order; }
    @SerializedName("order")
    public void setOrder(long value) { this.order = value; }

    @SerializedName("id")
    public String getId() { return id; }
    @SerializedName("id")
    public void setId(String value) { this.id = value; }

    @SerializedName("name")
    public String getName() { return name; }
    @SerializedName("name")
    public void setName(String value) { this.name = value; }

    @SerializedName("description")
    public String getDescription() { return description; }
    @SerializedName("description")
    public void setDescription(String value) { this.description = value; }

    @SerializedName("aliases")
    public String[] getAliases() { return aliases; }
    @SerializedName("aliases")
    public void setAliases(String[] value) { this.aliases = value; }

    @Nullable
    @SerializedName("questions")
    public String[] getQuestions() { return questions; }
    @SerializedName("questions")
    public void setQuestions(String[] value) { this.questions = value; }

}

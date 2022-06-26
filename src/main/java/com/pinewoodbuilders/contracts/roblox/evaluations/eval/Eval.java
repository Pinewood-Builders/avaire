package com.pinewoodbuilders.contracts.roblox.evaluations.eval;

import com.google.gson.annotations.SerializedName;

public class Eval {

    public Eval(String id, String name, String description, String[] aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    private String id;
    private String name;
    private String description;
    private String[] aliases;

    @SerializedName("id")
    public String getID() { return id; }
    @SerializedName("id")
    public void setID(String value) { this.id = value; }

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
}


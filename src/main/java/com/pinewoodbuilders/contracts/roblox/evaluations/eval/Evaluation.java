package com.pinewoodbuilders.contracts.roblox.evaluations.eval;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Evaluation {private String id;
    private String name;
    private String description;
    private List <String> aliases;

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
    public List<String> getAliases() { return aliases; }
    @SerializedName("aliases")
    public void setAliases(List<String> value) { this.aliases = value; }
}

package com.pinewoodbuilders.contracts.roblox.evaluations.settings;

import com.pinewoodbuilders.contracts.roblox.evaluations.eval.Evaluation;

import java.util.List;

public class RankSettingBuilder {
    private long order;
    private String id;
    private String name;
    private List<String> aliases;
    private String description;
    private List<Evaluation> evaluations;

    public RankSettingBuilder setOrder(long order) {
        this.order = order;
        return this;
    }

    public RankSettingBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public RankSettingBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public RankSettingBuilder setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public RankSettingBuilder addAlias(String alias) {
        this.aliases.add(alias);
        return this;
    }

    public RankSettingBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public RankSettingBuilder setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
        return this;
    }

    public RankSetting build() {
        return new RankSetting(order, id, name, aliases, description, evaluations);
    }
}

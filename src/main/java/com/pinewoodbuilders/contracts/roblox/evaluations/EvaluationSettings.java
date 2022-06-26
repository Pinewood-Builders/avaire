package com.pinewoodbuilders.contracts.roblox.evaluations;

import com.google.gson.annotations.SerializedName;
import com.pinewoodbuilders.contracts.roblox.evaluations.eval.Eval;

import java.util.List;

public class EvaluationSettings {

    private List<Eval> evals;

    @SerializedName("evals")
    public List <Eval> getEvals() { return evals; }
    @SerializedName("evals")
    public void setEvals(List<Eval> value) { this.evals = value; }
}

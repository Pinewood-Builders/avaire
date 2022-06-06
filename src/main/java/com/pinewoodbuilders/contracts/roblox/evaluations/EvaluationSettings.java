package com.pinewoodbuilders.contracts.roblox.evaluations;

import com.pinewoodbuilders.Xeus;
import org.json.JSONObject;

import java.util.HashMap;

public class EvaluationSettings {
    public final String id;
    public final String name;
    public final int askedQuestions;
    public JSONObject evaluations = null;

    public EvaluationSettings(String id, String name, int askedQuestions, String evaluations) {
        this.id = id;
        this.name = name;
        this.askedQuestions = askedQuestions;
        this.evaluations = initEvals(new JSONObject(evaluations));
    }

    private JSONObject initEvals(JSONObject evaluations) {
        HashMap<String, String> map = new HashMap<>();
        if (evaluations.) {
            return new JSONObject(Xeus.gson.toJson(map));
        }


    }

}

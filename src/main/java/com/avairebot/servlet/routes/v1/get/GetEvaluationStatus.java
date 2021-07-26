package com.avairebot.servlet.routes.v1.get;

import com.avairebot.AvaIre;
import com.avairebot.Constants;
import com.avairebot.contracts.metrics.SparkRoute;
import com.avairebot.database.collection.Collection;
import com.avairebot.database.collection.DataRow;
import com.avairebot.database.controllers.GuildController;
import com.avairebot.database.transformers.GuildTransformer;
import com.avairebot.requests.service.user.rank.RobloxUserGroupRankService;
import com.google.common.cache.Cache;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.stream.Collectors;

public class GetEvaluationStatus extends SparkRoute {

    private final Logger log = LoggerFactory.getLogger(GetEvaluationStatus.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (!hasValidEvaluationsAuthorizationHeader(request)) {
            log.warn("Unauthorized request, missing or invalid `Authorization` header give.");
            return buildResponse(response, 401, "Unauthorized request, missing or invalid `Authorization` header give.");
        }

        Long guildId = Long.valueOf(request.params("guildId"));
        Long robloxId = Long.valueOf(request.params("robloxId"));
        JSONObject root = new JSONObject();

        if (AvaIre.getInstance().getRobloxAPIManager().getUserAPI().getUsername(robloxId) == null) {
            response.status(404);
            root.put("error", 404);
            root.put("message", "Sorry, but the ID of this user doesn't exist. Please try again later.");
            return root;
        }

        Collection collection = AvaIre.getInstance().getDatabase().newQueryBuilder(Constants.EVALS_DATABASE_TABLE_NAME).where("roblox_id", robloxId).get();
        if (collection.size() < 1) {
            root.put("passed_quiz", false);
            root.put("passed_combat", false);
            root.put("passed_patrol", false);
            root.put("quizPending", AvaIre.getInstance().getRobloxAPIManager().getEvaluationManager().hasPendingQuiz(robloxId));
            root.put("enoughPoints", checkPoints(robloxId));
            root.put("rankLocked", AvaIre.getInstance().getRobloxAPIManager().getKronosManager().isRanklocked(robloxId));
            root.put("onCooldown", getCooldownFromCache(robloxId));
            root.put("isEvalRank", isEvalRank(guildId, robloxId));
            root.put("roblox_id", robloxId);
            return root;
        }

        if (collection.size() > 2) {
            response.status(500);
            root.put("error", 500);
            root.put("message", "There has been a mistake in the database, please contact the Xeus administrator to solve this issue.");
            return root;
        }

        DataRow row = collection.get(0);
        Boolean pq = row.getBoolean("passed_quiz");
        Boolean pp = row.getBoolean("passed_patrol");
        Boolean pc = row.getBoolean("passed_combat");


        String evaluator = row.getString("evaluator") != null ? row.getString("evaluator") : "Unkown Evaluator";

        root.put("passed_quiz", pq);
        root.put("passed_patrol", pp);
        root.put("passed_combat", pc);
        root.put("evaluator", evaluator);
        root.put("roblox_id", robloxId);
        root.put("quizPending", AvaIre.getInstance().getRobloxAPIManager().getEvaluationManager().hasPendingQuiz(robloxId));
        root.put("enoughPoints", checkPoints(robloxId));
        root.put("rankLocked", AvaIre.getInstance().getRobloxAPIManager().getKronosManager().isRanklocked(robloxId));
        root.put("onCooldown", getCooldownFromCache(robloxId));
        root.put("isEvalRank", isEvalRank(guildId, robloxId));
        return root;
    }

    private boolean checkPoints(Long robloxId) {
        long points = AvaIre.getInstance().getRobloxAPIManager().getKronosManager().getPoints(robloxId);
        return points >= 75;
    }

    private boolean isEvalRank(Long id, Long robloxId) {
        Guild guild = AvaIre.getInstance().getShardManager().getGuildById(id);
        if (guild != null) {
            if (guild.getId().equals("438134543837560832")) {
                GuildTransformer transformer = GuildController.fetchGuild(AvaIre.getInstance(), guild);
                if (transformer != null) {
                    if (transformer.getRobloxGroupId() != 0) {
                        List <RobloxUserGroupRankService.Data> ranks = AvaIre.getInstance().getRobloxAPIManager()
                            .getUserAPI().getUserRanks(robloxId).stream()
                            .filter(groupRanks -> groupRanks.getGroup().getId() == transformer.getRobloxGroupId())
                            .collect(Collectors.toList());
                        if (ranks.size() == 0) {
                            return false;
                        }

                        return ranks.get(0).getRole().getRank() == 2;
                    }
                }
            }
        }
        return true;
    }

    private boolean getCooldownFromCache(Long robloxId) {
        Cache <String, Boolean> cache = AvaIre.getInstance().getRobloxAPIManager().getEvaluationManager().getCooldownCache();
        return cache.getIfPresent("evaluation." + robloxId + ".cooldown") != null;
    }
}

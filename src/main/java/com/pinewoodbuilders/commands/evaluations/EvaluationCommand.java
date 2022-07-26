package com.pinewoodbuilders.commands.evaluations;

import com.pinewoodbuilders.AppInfo;
import com.pinewoodbuilders.Constants;
import com.pinewoodbuilders.Xeus;
import com.pinewoodbuilders.commands.CommandMessage;
import com.pinewoodbuilders.contracts.commands.Command;
import com.pinewoodbuilders.contracts.commands.CommandGroup;
import com.pinewoodbuilders.contracts.commands.CommandGroups;
import com.pinewoodbuilders.contracts.roblox.evaluations.EvaluationSettings;
import com.pinewoodbuilders.contracts.roblox.evaluations.EvaluationStatus;
import com.pinewoodbuilders.contracts.roblox.evaluations.settings.RankSetting;
import com.pinewoodbuilders.contracts.roblox.evaluations.settings.RankSettingBuilder;
import com.pinewoodbuilders.database.collection.Collection;
import com.pinewoodbuilders.database.collection.DataRow;
import com.pinewoodbuilders.database.controllers.GroupSettingsController;
import com.pinewoodbuilders.database.transformers.GroupSettingsTransformer;
import com.pinewoodbuilders.database.transformers.GuildSettingsTransformer;
import com.pinewoodbuilders.utilities.NumberUtil;
import com.pinewoodbuilders.utilities.menu.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pinewoodbuilders.utilities.JsonReader.readJsonFromUrl;

public class EvaluationCommand extends Command {
    private final Paginator.Builder builder;

    public EvaluationCommand(Xeus avaire) {
        super(avaire);
        builder = new Paginator.Builder()
            .setColumns(1)
            .setFinalAction(m -> {try {m.clearReactions().queue();} catch (PermissionException ignore) {}})
            .setItemsPerPage(10)
            .waitOnSinglePage(false)
            .useNumberedItems(true)
            .showPageNumbers(true)
            .wrapPageEnds(true)
            .setEventWaiter(avaire.getWaiter())
            .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public String getName() {
        return "Evaluation Command";
    }

    @Override
    public String getDescription() {
        return "Commands to manage the evaluations.";
    }

    @Override
    public List <String> getUsageInstructions() {
        return Arrays.asList(
            "`:command <roblox username>` - Get the eval status of a user.",
            "`:command <roblox username> failed/passed quiz/patrol` - Fail/Succeed someone for a Quiz/Patrol"
        );
    }

    @Override
    public List <String> getExampleUsage() {
        return Arrays.asList(
            "`:command superstefano4` - Get the eval status of **superstefano4**.",
            "`:command Cdsi passed quiz` - Succeed **Csdi** for a Quiz"
        );
    }


    @Override
    public List <String> getTriggers() {
        return Arrays.asList("evaluation", "evals");
    }

    @Nonnull
    @Override
    public List <CommandGroup> getGroups() {
        return Collections.singletonList(
            CommandGroups.EVALUATIONS
        );
    }

    @Override
    public List <String> getMiddleware() {
        return Arrays.asList(
            "isPinewoodGuild",
            "isGuildHROrHigher",
            "throttle:user,1,3"
        );
    }

    @Override
    public boolean onCommand(CommandMessage context, String[] args) {
        if (args.length < 1) {
            context.makeError("Invalid usage of command. Please add the required arguments. (Begin with the roblox " +
                "name)").queue();
            return false;
        }

        return switch (args[0].toLowerCase()) {
            case "settings" -> runSettings(context, Arrays.copyOfRange(args, 1, args.length));
            case "set-quiz-channel", "sqc", "quiz-channel", "qc" -> setQuizChannel(context, args);
            case "questions" -> questionSubMenu(context, args);
            case "kronos-sync" -> runKronosSync(context);
            case "oh-no" -> ohNo(context);
            case "oh-yes" -> ohYes(context);
            default -> evalSystemCommands(context, args);
        };
    }

    private boolean runSettings(CommandMessage context, String[] args) {
        GroupSettingsTransformer guildSettings = GroupSettingsController.fetchGroupSettingsFromGroupSettings(avaire, context.getGuildSettingsTransformer());
        if (guildSettings == null) {return sendErrorMessage(context, "The GroupSettingsTransformer is null, please try again later.");}

        EvaluationSettings evalSettings = guildSettings.getEvaluationSettings();
        if (evalSettings == null) {return sendErrorMessage(context, "The EvaluationSettings is null, please try again later.");}

        switch (args[0]) {
            case "list" -> listEvaluations(context, evalSettings);
            case "modify" -> modifyEvaluations(context, evalSettings, Arrays.copyOfRange(args, 1, args.length));
        }

        return false;
    }

    private void modifyEvaluations(CommandMessage context, EvaluationSettings evalSettings, String[] args) {
        if (args.length < 1) {
            context.makeError("Invalid usage of command. Please add the required arguments. (Begin with the roblox " +
                "name)").queue();
            return;
        }

        switch (args[1]) {
            case "add" -> addEvaluation(context, evalSettings, Arrays.copyOfRange(args, 1, args.length));
            case "remove" -> removeEvaluation(context, evalSettings, Arrays.copyOfRange(args, 1, args.length));
            case "clear" -> clearEvaluation(context, evalSettings, Arrays.copyOfRange(args, 1, args.length));
        }
    }

    private void clearEvaluation(CommandMessage context, EvaluationSettings evalSettings, String[] args) {
    }

    private void removeEvaluation(CommandMessage context, EvaluationSettings evalSettings, String[] args) {

    }

    //!evals settings add <type> <id> <aliases> <eval> <user>
    private void addEvaluation(CommandMessage context, EvaluationSettings evalSettings, String[] args) {
        RankSettingBuilder rs = new RankSettingBuilder();
        rs.addAlias("owo");
    }

    private void listEvaluations(CommandMessage context, EvaluationSettings evalSettings) {
        List<MessageEmbed> messageEmbeds = new ArrayList <>();
        for (RankSetting rankSetting : evalSettings.getRankSettings()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setDescription("**%s** (`%d`):\n - `ID`: %s\n - `Aliases`: %s\n - `Evaluations`: %s\n\n".formatted(rankSetting.getName(),
                rankSetting.getOrder(), rankSetting.getId(),
                rankSetting.getAliases().toString(),
                String.join("", rankSetting.getEvaluations().stream()
                    .map(evaluation -> "\n```  %s (%d):\n   - ID: %s\n   - Aliases: %s```"
                        .formatted(evaluation.getName(),
                            evaluation.getOrder(),
                            evaluation.getId(),
                            Arrays.toString(evaluation.getAliases()))).toList()
            ))).setFooter(rankSetting.getId() + " | " + rankSetting.getName() + " | " + rankSetting.getOrder());

            messageEmbeds.add(eb.build());
        }
        context.getTextChannel().sendMessageEmbeds(messageEmbeds).queue();
    }

    private boolean ohYes(CommandMessage context) {
        // Send a json request to the following raw url: https://pastebin.com/raw/Yscb67wh
        // The json will be a list of long values, each long being an roblox user id.
        // Every ID in that list get's checked on their eval status, and if they passed both the quiz and the combat.
        // If they passed both, they get a DM with a message saying they passed the quiz and the combat.
        Request.Builder request = new Request.Builder()
            .addHeader("User-Agent", "Xeus v" + AppInfo.getAppInfo().version)
            .url("https://pastebin.com/raw/Yscb67wh");

        try (Response response = avaire.getRobloxAPIManager().getClient().newCall(request.build()).execute()) {
            if (response.code() == 200) {
                String body = response.body().string();
                JSONArray array = new JSONArray(body);
                List<Long> ids = array.toList().stream().map(Object::toString).map(Long::parseLong).toList();

                for (Long id : ids) {
                    EvaluationStatus status = avaire.getRobloxAPIManager().getEvaluationManager().getEvaluationStatus(id);
                    if (status.passedQuiz() && status.passedCombat() && !status.passedConsensus()) {
                        String username = avaire.getRobloxAPIManager().getUserAPI().getUsername(id);
                        context.makeInfo("""
                            **:username**

                            Quiz: **:status**
                            Combat: **:status**

                            Verdict: **N.A.**""")
                            .set("username", username)
                            .set("status", "Passed").queue(
                                message -> {
                                    message.createThreadChannel(username).queue();
                                    message.addReaction("\uD83D\uDC4D").queue(); //
                                    message.addReaction("✋").queue(); //
                                    message.addReaction("\uD83D\uDC4E").queue(); //
                                }
                            );

                    }
                }
            }
        } catch (IOException e) {
            Xeus.getLogger().error("Failed sending request to Roblox API: " + e.getMessage());
        }
        return true;
    }

    private boolean ohNo(CommandMessage context) {
        // Send a json request to the following raw url: https://pastebin.com/raw/Yscb67wh
        // The json will be a list of long values, each long being an roblox user id.
        // Every ID will be checked to see if they have passed all evals, and if they haven't. Put their ID in an array.
        // The array will be sent to the following url: https://pastebin.com/raw/Yscb67wh

        Request.Builder request = new Request.Builder()
            .addHeader("User-Agent", "Xeus v" + AppInfo.getAppInfo().version)
            .url("https://pastebin.com/raw/Yscb67wh");

        try (Response response = avaire.getRobloxAPIManager().getClient().newCall(request.build()).execute()) {
            if (response.code() == 200) {
                String body = response.body().string();
                JSONArray array = new JSONArray(body);
                List<Long> ids = array.toList().stream().map(Object::toString).map(Long::parseLong).toList();

                List<Long> failed = new ArrayList <>();

                for (Long id : ids) {
                    EvaluationStatus status = avaire.getRobloxAPIManager().getEvaluationManager().getEvaluationStatus(id);
                    if (!status.isPassed()) {
                        failed.add(id);
                    }
                }

                System.out.println(failed);
            }
        } catch (IOException e) {
            Xeus.getLogger().error("Failed sending request to Roblox API: " + e.getMessage());
        }
        return true;
    }

    private boolean runKronosSync(CommandMessage context) {
        try {
            Collection collection = avaire.getDatabase().newQueryBuilder(Constants.EVALS_DATABASE_TABLE_NAME).get();
            context.makeInfo("Syncing `" + collection.size() + "` eval records to Kronos").queue();
            for (DataRow dr : collection) {
                Long robloxId = dr.getLong("roblox_id");
                EvaluationStatus status = avaire.getRobloxAPIManager().getEvaluationManager().getEvaluationStatus(robloxId);

                if (status == null) {
                    continue;
                }

                avaire.getRobloxAPIManager().getKronosManager().modifyEvalStatus(dr.getLong("roblox_id"), "pbst", status.isPassed());
            }
            context.makeSuccess("Synced data with Kronos!").queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean questionSubMenu(CommandMessage context, String[] args) {
        if (args.length < 2) {
            context.makeError("Would you like to `add` or `remove` a question? Or would you like to `list` all questions?").queue();
            return false;
        }

        switch (args[1]) {
            case "add":
                return addQuestionToGuildQuestions(context, args);
            case "remove":
                return removeQuestionFromGuildQuestions(context, args);
            case "list":
                return listQuestions(context, args);
            default:
                context.makeError("Would you like to `add` or `remove` a question? Or would you like to `list` all questions?").queue();
                return false;
        }
    }

    private boolean listQuestions(CommandMessage context, String[] args) {
        GuildSettingsTransformer transformer = context.getGuildSettingsTransformer();
        if (transformer == null) {
            context.makeError("The GuildSettingsTransformer is null, please try again later.").queue();
            return false;
        }

        if (transformer.getEvalQuestions().size() < 1) {
            context.makeError("The questions is are empty or null, please fill the questions for this guild.").queue();
            return false;
        }


        builder.setText("Current questions in the list: ")
            .setItems(transformer.getEvalQuestions())
            .setUsers(context.getAuthor())
            .setColor(context.getGuild().getSelfMember().getColor());

        builder.build().paginate(context.getChannel(), 0);
        return true;
    }

    private boolean addQuestionToGuildQuestions(CommandMessage context, String[] args) {
        if (args.length < 3) {
            context.makeInfo("Run the command again, and make sure you add the question you want to add.").queue();
            return false;
        }
        GuildSettingsTransformer transformer = context.getGuildSettingsTransformer();
        if (transformer == null) {
            context.makeError("The GuildSettingsTransformer is null, please try again later.").queue();
            return false;
        }

        String question = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (transformer.getEvalQuestions().contains(question)) {
            context.makeError("This question already exists in the database.").queue();
            return false;
        }

        transformer.getEvalQuestions().add(question);
        try {
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_SETTINGS_TABLE).where("id", context.getGuild().getId())
                .update(statement -> statement.set("eval_questions", Xeus.gson.toJson(transformer.getEvalQuestions()), true));
            context.makeSuccess("Added `:question` to the database!").set("question", question).queue();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            context.makeError("Something went wrong adding the question to the database.").queue();
            return false;
        }

        return true;
    }

    private boolean removeQuestionFromGuildQuestions(CommandMessage context, String[] args) {
        if (args.length < 3) {
            context.makeInfo("Run the command again, and make sure you add the question you want to remove.").queue();
            return false;
        }
        GuildSettingsTransformer transformer = context.getGuildSettingsTransformer();
        if (transformer == null) {
            context.makeError("The GuildSettingsTransformer is null, please try again later.").queue();
            return false;
        }

        String question = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (!transformer.getEvalQuestions().contains(question)) {
            context.makeError("This question doesn't exist in the database.").queue();
            return false;
        }

        transformer.getEvalQuestions().remove(question);
        try {
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_SETTINGS_TABLE).where("id", context.getGuild().getId())
                .update(statement -> statement.set("eval_questions", Xeus.gson.toJson(transformer.getEvalQuestions()), true));
            context.makeSuccess("Removed `:question` from the database!").set("question", question).queue();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            context.makeError("Something went wrong removing the question from the database.").queue();
            return false;
        }
        return true;
    }

    private boolean setQuizChannel(CommandMessage context, String[] args) {
        if (args.length < 2) {
            context.makeError("I'm missing the channel ID, please give me the ID.").queue();
            return false;
        }
        if (!NumberUtil.isNumeric(args[1])) {
            context.makeError("I need a channel ***ID***, not the name or anything else.").queue();
            return false;
        }

        TextChannel tc = context.getGuild().getTextChannelById(args[1]);
        if (tc == null) {
            context.makeError("The ID you gave me is invalid... Are you really this stupid?").queue();
            return false;
        }

        try {
            context.getGuildSettingsTransformer().setEvaluationEvalChannel(Long.parseLong(args[1]));
            avaire.getDatabase().newQueryBuilder(Constants.GUILD_SETTINGS_TABLE).where("id", context.getGuild().getId()).update(statement -> {
                statement.set("evaluation_answer_channel", context.getGuildSettingsTransformer().getEvaluationEvalChannel());
            });
            context.makeSuccess("Eval answers channel has been set to :channelName.").set("channelName",
                tc.getAsMention()).queue();

            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            context.makeError("Something went wrong...").queue();
            return false;
        }
    }

    private boolean evalSystemCommands(CommandMessage context, String[] args) {
        if (!isValidRobloxUser(args[0])) {
            context.makeError("This user is not a valid robloxian.").queue();
            return false;
        }

        GuildSettingsTransformer guildSettings = context.getGuildSettingsTransformer();
        if (guildSettings == null) {return sendErrorMessage(context, "The GuildSettingsTransformer is null, please try again later.");}



        if (args.length < 2) {
            context.makeError("I'm missing an additional argument, do you want to `pass` or `fail` **" + args[0] + "**?").queue();
            return false;
        }

        switch (args[1].toLowerCase()) {
            case "pass":
                return modifyEvalStatus(context, args, true);
            case "fail":
                return modifyEvalStatus(context, args, false);
            default:
                context.makeError("I don't know what you want to do, please use `pass` or `fail`.").queue();
                return false;
        }

    }

    private boolean modifyEvalStatus(CommandMessage context, String[] args, boolean b) {
        if (args.length < 3) {
            context.makeError("");
        }
return false;
    }

    private static String getRobloxUsernameFromId(Long id) {
        try {
            JSONObject json = readJsonFromUrl("https://api.roblox.com/users/" + id);
            return json.getString("Username");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public Long getRobloxId(String un) {
        try {
            return avaire.getRobloxAPIManager().getUserAPI().getIdFromUsername(un);
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean isValidRobloxUser(String un) {
        try {
            return avaire.getRobloxAPIManager().getUserAPI().getIdFromUsername(un) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}

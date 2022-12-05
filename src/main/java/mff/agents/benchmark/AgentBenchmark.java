package mff.agents.benchmark;

import engine.core.MarioAgent;
import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;
import mff.agents.common.IMarioAgentMFF;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class AgentBenchmark {

    private static final DecimalFormat twoFractionDigitsCommaSeparator;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.');
        twoFractionDigitsCommaSeparator = new DecimalFormat("0.00", symbols);
    }

    private static final ArrayList<String> agents = new ArrayList<>() {{
//        add("robinBaumgarten");
//        add("robinBaumgartenSlimWindowAdvance");
        add("astar");
//        add("astarPlanningDynamic");
//        add("astarWindow");
    }};

    public static void main(String[] args) throws IOException {
        for (var agentType : agents) {
            File log = prepareLog("agent-benchmark" + File.separator + agentType + ".csv");
            if (log == null)
                return;
            FileWriter logWriter = new FileWriter(log);

            logWriter.write("level,win/fail,% travelled,run time,game ticks,planning time,total plannings,nodes evaluated\n");

//            warmup(agentType);
            testOriginalLevels(agentType, logWriter);
//            testKrysLevels(agentType, logWriter);

            logWriter.close();
        }
    }

    private static void testKrysLevels(String agentType, FileWriter log) throws IOException {
        AgentStats agentStats;
        if (!agentType.equals("robinBaumgarten")) {
            for (int i = 1; i <= 100; i++) {
                System.out.println(agentType + "-" + "krys" + "-" + i);
                MarioLevelGenerator generator = new levelGenerators.krys.LevelGenerator(i);
                String level = generator.getGeneratedLevel(new MarioLevelModel(150, 16),
                        new MarioTimer(5 * 60 * 60 * 1000));
                AgentBenchmarkGame game = new AgentBenchmarkGame();
                IMarioAgentMFF agent = getNewAgent(agentType);
                // only 30 seconds to speed-up timeout if agent is stuck
                agentStats = game.runGame(agent, level, 30, 0, false);
                agentStats.level = "Krys-" + i;
                printStats(log, agentStats);
            }
        }
        else {
            for (int i = 1; i <= 100; i++) {
                System.out.println(agentType + "-" + "krys" + "-" + i);
                MarioLevelGenerator generator = new levelGenerators.krys.LevelGenerator(i);
                String level = generator.getGeneratedLevel(new MarioLevelModel(150, 16),
                        new MarioTimer(5 * 60 * 60 * 1000));
                OriginalAgentBenchmarkGame game = new OriginalAgentBenchmarkGame();
                MarioAgent agent = new agents.robinBaumgarten.Agent();
                // only 30 seconds to speed-up timeout if agent is stuck
                agentStats = game.runGame(agent, level, 30, 0, false);
                agentStats.level = "Krys-" + i;
                printStats(log, agentStats);
            }
        }
    }

    private static void testOriginalLevels(String agentType, FileWriter log) throws IOException {
        AgentStats agentStats;
        if (!agentType.equals("robinBaumgarten")) {
            for (int i = 1; i < 2; i++) {
                System.out.println(agentType + "-" + "original" + "-" + i);
                AgentBenchmarkGame game = new AgentBenchmarkGame();
                IMarioAgentMFF agent = getNewAgent(agentType);
                String level = getLevel("./levels/original/lvl-" + i + ".txt");
                agentStats = game.runGame(agent, level,200, 0, false);
                agentStats.level = "Mario-" + i;
                printStats(log, agentStats);
            }
        }
        else {
            for (int i = 1; i < 16; i++) {
                System.out.println(agentType + "-" + "original" + "-" + i);
                OriginalAgentBenchmarkGame game = new OriginalAgentBenchmarkGame();
                MarioAgent agent = new agents.robinBaumgarten.Agent();
                String level = getLevel("./levels/original/lvl-" + i + ".txt");
                agentStats = game.runGame(agent, level, 200, 0, false);
                agentStats.level = "Mario-" + i;
                printStats(log, agentStats);
            }
        }
    }

    private static void warmup(String agentType) {
        System.out.println("WARMUP: " + agentType + "-" + "original-1");
        if (!agentType.equals("robinBaumgarten")) {
            AgentBenchmarkGame game = new AgentBenchmarkGame();
            IMarioAgentMFF agent = getNewAgent(agentType);
            String level = getLevel("./levels/original/lvl-1.txt");
            game.runGame(agent, level,200, 0, false);
        }
        else {
            OriginalAgentBenchmarkGame game = new OriginalAgentBenchmarkGame();
            MarioAgent agent = new agents.robinBaumgarten.Agent();
            String level = getLevel("./levels/original/lvl-1.txt");
            game.runGame(agent, level, 200, 0, false);
        }
    }

    private static void printStats(FileWriter writer, AgentStats stats) throws IOException {
        writer.write(stats.level + ','
                + stats.win + ','
                + twoFractionDigitsCommaSeparator.format(stats.percentageTravelled) + ','
                + stats.runTime + ','
                + stats.totalGameTicks + ','
                + stats.totalPlanningTime + ','
                + stats.searchCalls + ','
                + stats.nodesEvaluated + '\n'
        );
    }

    private static File prepareLog(String name) throws IOException {
        File log = new File(name);
        if (log.exists()) {
            if (!log.delete()) {
                System.out.println("Can't delete file: " + log.getName());
                return null;
            }
        }
        if (!log.createNewFile()) {
            System.out.println("Can't create file: " + log.getName());
            return null;
        }
        return log;
    }

    private static IMarioAgentMFF getNewAgent(String agentType) {
        switch (agentType) {
            case "astar":
                return new mff.agents.astar.Agent();
            case "astarDistanceMetric":
                return new mff.agents.astarDistanceMetric.Agent();
            case "astarPlanningDynamic":
                return new mff.agents.astarPlanningDynamic.Agent();
            case "astarWindow":
                return new mff.agents.astarWindow.Agent();
            case "robinBaumgartenSlim":
                return new mff.agents.robinBaumgartenSlim.Agent();
            case "robinBaumgartenSlimImproved":
                return new mff.agents.robinBaumgartenSlimImproved.Agent();
            case "robinBaumgartenSlimWindowAdvance":
                return new mff.agents.robinBaumgartenSlimWindowAdvance.Agent();
            default:
                throw new IllegalArgumentException("Agent not supported.");
        }
    }

    private static String getLevel(String filepath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filepath)));
            return content;
        } catch (IOException ignored) {
            // try with working directory set one folder down
        }
        try {
            content = new String(Files.readAllBytes(Paths.get("." + filepath)));
        }
        catch (IOException e) {
            System.out.println("Level couldn't be loaded, please check the path provided with regards to your working directory.");
            System.exit(1);
        }
        return content;
    }
}

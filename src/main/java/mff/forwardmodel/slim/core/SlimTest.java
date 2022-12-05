package mff.forwardmodel.slim.core;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SlimTest {
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

    public static void main(String[] args) {
        //correctnessTest();
        advanceSpeedTest();
    }

    private static void correctnessTest() {
        for (int i = 1; i < 16; i++) {
            MarioGameSlim game = new MarioGameSlim(true, false);
            game.runGame(new mff.agents.astar.Agent(), getLevel("./levels/original/lvl-" + i + ".txt"), 200, 0, false);
        }
    }

    private static void advanceSpeedTest() {
        for (int i = 1; i <= 35; i++) {
            double originalTime = 0;
            double slimTime = 0;
            double slimWindowTime = 0;

            String level;

            if (i < 16) {
                level = getLevel("./levels/original/lvl-" + i + ".txt");
            } else if (i < 26) {
                MarioLevelGenerator generator = new levelGenerators.notch.LevelGenerator();
                try {
                    level = generator.getGeneratedLevel(new MarioLevelModel(150, 16), new MarioTimer(5 * 60 * 60 * 1000));
                } catch (IllegalArgumentException e) {
                    System.out.println("Level generator failed.");
                    continue;
                }
            } else {
                MarioLevelGenerator generator = new levelGenerators.benWeber.LevelGenerator();
                level = generator.getGeneratedLevel(new MarioLevelModel(150, 16), new MarioTimer(5 * 60 * 60 * 1000));
            }

            for (int j = 0; j < 10; j++) {
                MarioGameSlim game = new MarioGameSlim(false, true);
                game.runGame(new mff.agents.astar.Agent(), level, 200, 0, false);
            }
            for (int k = 0; k < 100; k++) {
                MarioGameSlim game = new MarioGameSlim(false, true);
                TestResult testResult = game.runGame(new mff.agents.astar.Agent(), level, 200, 0, false);
                originalTime += testResult.originalTime;
                slimTime += testResult.slimTime;
                slimWindowTime += testResult.slimWindowTime;
            }

            if (i < 16)
                System.out.println("orig - " + i + "," + originalTime + "," + slimTime + "," + slimWindowTime);
            else if (i < 26)
                System.out.println("notch - " + (i - 15) + "," + originalTime + "," + slimTime + "," + slimWindowTime);
            else
                System.out.println("benWeber - " + (i - 25) + "," + originalTime + "," + slimTime + "," + slimWindowTime);

//          System.out.println("Level: " + i + " original update time: " + originalTime);
//          System.out.println("Level: " + i + " slim update time: " + slimTime);
//          System.out.println();
//          System.out.print("Level " + i + ": ");
        }
    }
}

package levelGenerators.krys;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static levelGenerators.krys.BlockType.*;

enum BlockCategory {
    COMMON,
    THEME,
    MIX,
}

enum DifficultyType {
    INTRO,
    MID,
    HARD,
}

enum BlockType {
    COMM_END,
    COMM_START,
    COMM_FUN,
    THEME_SPIKY,
    THEME_BULLET,
    THEME_JUMP,
    THEME_PIPE,
    MIX_SPIKY_BULLET,
    MIX_SPIKY_JUMP,
    MIX_SPIKY_PIPE,
    MIX_BULLET_JUMP,
    MIX_BULLET_PIPE,
    MIX_JUMP_PIPE,
}

enum HeightType {
    LOW,
    MID,
    HIGH,
}

class LevelBlock {
    public LevelBlock(List<String> level, BlockCategory category, BlockType type, HeightType entry, HeightType exit, DifficultyType diff) {
        if (level.size() != 16)
            throw new IllegalArgumentException("Block with non valid size " + id);

        for (String line : level
        ) {
            if (line.length() != 15)
                throw new IllegalArgumentException("Block with non valid size " + id + level.toString());
        }

        this.id = count;
        this.category = category;
        this.type = type;
        this.entry = entry;
        this.exit = exit;
        this.diff = diff;

        StringBuilder levelStr = new StringBuilder();
        for (String line : level
        ) {
            levelStr.append(line);
            levelStr.append("\n");
        }
        this.level = levelStr.toString();
        count++;
    }

    static int count = 1;
    BlockCategory category = BlockCategory.COMMON;
    DifficultyType diff = null;
    BlockType type = null;
    HeightType entry = HeightType.LOW;
    HeightType exit = HeightType.LOW;
    int id;
    String level;
}

public class LevelGenerator implements MarioLevelGenerator {
    private final int BLOCK_WIDTH = 15;

    private String folderName = "levels/krys/";
    private Random rnd;

    private Hashtable<BlockType, List<LevelBlock>> common = new Hashtable<BlockType, List<LevelBlock>>();
    private Hashtable<BlockType, List<LevelBlock>> theme = new Hashtable<BlockType, List<LevelBlock>>();
    private Hashtable<BlockType, List<LevelBlock>> mix = new Hashtable<BlockType, List<LevelBlock>>();
    private List<BlockType> usableThemes = new LinkedList<>();

    BlockType themeOne = BlockType.THEME_JUMP;
    BlockType themeTwo = THEME_SPIKY;
    BlockType mixTheme = null;

    private long seed;

    public LevelGenerator(long seed) {
        this("levels/krys/");
        this.seed = seed;
    }

    public LevelGenerator(String sampleFolder) {
        this.folderName = sampleFolder;

        //Preload blocks
        LoadCommon();
        LoadTheme();
        LoadMix();

        //Add themes
        usableThemes.add(THEME_SPIKY);
        usableThemes.add(BlockType.THEME_BULLET);
        usableThemes.add(BlockType.THEME_JUMP);
        usableThemes.add(BlockType.THEME_PIPE);
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        if (model.getHeight() != 16 || model.getWidth() % BLOCK_WIDTH != 0) {
            throw new IllegalArgumentException("Invalid model size. Height must be 16 and width must be dividable by 15");
        }

        rnd = new Random(seed); // set seed so that benchmarks can be repeated
        for (int i = 0; i < 1000; i++) { // try to prevent artifacts
            rnd.nextInt();
        }

        model.clearMap();

        List<Integer> usedIds = new LinkedList<>();
        InitThemes();


        int numberOfBlocks = model.getWidth() / BLOCK_WIDTH; //10
        int blockNumber = 0;

        //Start block
        LevelBlock block = common.get(BlockType.COMM_START).get(rnd.nextInt(common.get(BlockType.COMM_START).size()));
        model.copyFromString(blockNumber * BLOCK_WIDTH, 0, 0, 0, BLOCK_WIDTH, model.getHeight(), block.level);
        usedIds.add(block.id);
        blockNumber++;
        HeightType currentHeight = block.exit;

        //Easy introducing block for both main mechanics
        block = GetThemeBlock(themeOne, DifficultyType.INTRO, currentHeight, usedIds);
        model.copyFromString(blockNumber * BLOCK_WIDTH, 0, 0, 0, BLOCK_WIDTH, model.getHeight(), block.level);
        usedIds.add(block.id);
        blockNumber++;
        currentHeight = block.exit;

        block = GetThemeBlock(themeTwo, DifficultyType.INTRO, currentHeight, usedIds);
        model.copyFromString(blockNumber * BLOCK_WIDTH, 0, 0, 0, BLOCK_WIDTH, model.getHeight(), block.level);
        usedIds.add(block.id);
        blockNumber++;
        currentHeight = block.exit;

        //Ramp up difficulty block for both main mechanics, also chance of fun block. 2-3 blocks
        double ONE_CHANCE = 0.4;
        double TWO_CHANCE = 0.4;
        for (int i = 0; i < 3; i++) {
            double random = rnd.nextDouble();
            if (random < ONE_CHANCE) {
                block = GetThemeBlock(themeOne, DifficultyType.MID, currentHeight, usedIds);
            } else if (random < (ONE_CHANCE + TWO_CHANCE)) {
                block = GetThemeBlock(themeTwo, DifficultyType.MID, currentHeight, usedIds);
            } else {
                block = GetCommonBlock(COMM_FUN, currentHeight, usedIds);
            }
            model.copyFromString(blockNumber * BLOCK_WIDTH, 0, 0, 0, BLOCK_WIDTH, model.getHeight(), block.level);
            usedIds.add(block.id);
            blockNumber++;
            currentHeight = block.exit;
        }

        //Hard block with a chance of mix block, 3 blocks
        for (int i = 0; i < 3; i++) {
            double random = rnd.nextDouble();
            if (random < ONE_CHANCE) {
                block = GetThemeBlock(themeOne, DifficultyType.HARD, currentHeight, usedIds);
            } else if (random < (ONE_CHANCE + TWO_CHANCE) || mixTheme == null) {
                block = GetThemeBlock(themeTwo, DifficultyType.HARD, currentHeight, usedIds);
            } else {
                block = GetMixBlock(mixTheme, currentHeight, usedIds);
            }
            model.copyFromString(blockNumber * BLOCK_WIDTH, 0, 0, 0, BLOCK_WIDTH, model.getHeight(), block.level);
            usedIds.add(block.id);
            blockNumber++;
            currentHeight = block.exit;
        }

        //Final block
        LevelBlock end = GetCommonBlock(COMM_END, currentHeight, usedIds);
        model.copyFromString((numberOfBlocks - 1) * BLOCK_WIDTH, 0, 0, 0, BLOCK_WIDTH, model.getHeight(), end.level);

        return model.getMap();
    }

    private LevelBlock GetMixBlock(BlockType type, HeightType currentHeight, List<Integer> usedIds) {
        LevelBlock block = mix.get(type).get(rnd.nextInt(mix.get(type).size()));
        int i = 0;
        while (!CanTransition(currentHeight, block.entry) || (usedIds.contains(block.id) && i < 10)) {
            block = mix.get(type).get(rnd.nextInt(mix.get(type).size()));
            i++;
        }
        return block;
    }

    private LevelBlock GetCommonBlock(BlockType type, HeightType currentHeight, List<Integer> usedIds) {
        LevelBlock block = common.get(type).get(rnd.nextInt(common.get(type).size()));
        int i = 0;
        while (!CanTransition(currentHeight, block.entry) || (usedIds.contains(block.id) && i < 10)) {
            block = common.get(type).get(rnd.nextInt(common.get(type).size()));
            i++;
        }
        return block;
    }

    private LevelBlock GetThemeBlock(BlockType type, DifficultyType diff, HeightType currentHeight, List<Integer> usedIds) {
        LevelBlock block = theme.get(type).get(rnd.nextInt(theme.get(type).size()));
        int i = 0;
        while (!CanTransition(currentHeight, block.entry) || (block.diff != diff && i < 10)  || (usedIds.contains(block.id) && i < 10)) {
            block = theme.get(type).get(rnd.nextInt(theme.get(type).size()));
            i++;
        }
        return block;
    }

    private void InitThemes() {
        int first = rnd.nextInt(usableThemes.size());
        int second = rnd.nextInt(usableThemes.size());

        while (second == first) {
            second = rnd.nextInt(usableThemes.size());
        }

        if (second < first) {
            int tmp = first;
            first = second;
            second = tmp;
        }

        themeOne = usableThemes.get(first);
        themeTwo = usableThemes.get(second);

        if (themeOne == themeTwo) {
            mixTheme = null;
        } else {
            switch (themeOne) {
                case THEME_SPIKY:
                    switch (themeTwo) {
                        case THEME_BULLET:
                            mixTheme = MIX_SPIKY_BULLET;
                            break;
                        case THEME_JUMP:
                            mixTheme = MIX_SPIKY_JUMP;
                            break;
                        case THEME_PIPE:
                            mixTheme = MIX_SPIKY_PIPE;
                    }
                    break;
                case THEME_BULLET:
                    if (themeTwo == THEME_JUMP)
                        mixTheme = MIX_BULLET_JUMP;
                    else
                        mixTheme = MIX_BULLET_PIPE;
                    break;
                case THEME_JUMP:
                    mixTheme = MIX_JUMP_PIPE;
            }
        }

        //Scramble chance
        if (rnd.nextDouble() < 0.5) {
            BlockType tmp = themeOne;
            themeOne = themeTwo;
            themeTwo = tmp;
        }
    }

    private boolean CanTransition(HeightType currentHeight, HeightType entry) {
        return currentHeight == entry || currentHeight == HeightType.HIGH || (currentHeight == HeightType.MID && entry == HeightType.LOW);
    }

    @Override
    public String getGeneratorName() {
        return "KrysLevelGenerator";
    }

    private void LoadCommon() {
        List<LevelBlock> start = new LinkedList<>();
        List<LevelBlock> end = new LinkedList<>();
        List<LevelBlock> fun = new LinkedList<>();
        File[] listOfFiles = new File(folderName + "common/").listFiles();
        assert listOfFiles != null;
        for (File file : listOfFiles
        ) {
            String name = file.getName();
            String[] splitted = name.split("_");
            try {
                List<String> level = Files.readAllLines(file.toPath());
                List<LevelBlock> addTo = fun;
                BlockType type = BlockType.COMM_FUN;
                switch (splitted[0]) {
                    case "S":
                        type = BlockType.COMM_START;
                        addTo = start;
                        break;
                    case "E":
                        type = BlockType.COMM_END;
                        addTo = end;
                        break;
                }

                HeightType entry = StringToHeight(splitted[1]);
                HeightType exit = StringToHeight(splitted[2]);
                LevelBlock block = new LevelBlock(level, BlockCategory.COMMON, type, entry, exit, null);
                addTo.add(block);
            } catch (IOException e) {
                e.printStackTrace();
            }

            common.put(BlockType.COMM_START, start);
            common.put(BlockType.COMM_END, end);
            common.put(BlockType.COMM_FUN, fun);
        }
    }

    private void LoadTheme() {
        List<LevelBlock> spiky = new LinkedList<>();
        List<LevelBlock> bullet = new LinkedList<>();
        List<LevelBlock> jump = new LinkedList<>();
        List<LevelBlock> pipe = new LinkedList<>();

        File[] listOfFiles = new File(folderName + "theme/").listFiles();

        for (File file : listOfFiles
        ) {
            String name = file.getName();
            String[] splitted = name.split("_");
            try {
                List<String> level = Files.readAllLines(file.toPath());
                BlockType type = THEME_SPIKY;
                List<LevelBlock> addTo = spiky;
                switch (splitted[0]) {
                    case "B":
                        type = BlockType.THEME_BULLET;
                        addTo = bullet;
                        break;
                    case "J":
                        type = BlockType.THEME_JUMP;
                        addTo = jump;
                        break;
                    case "P":
                        type = BlockType.THEME_PIPE;
                        addTo = pipe;
                }

                HeightType entry = StringToHeight(splitted[1]);
                HeightType exit = StringToHeight(splitted[2]);
                DifficultyType diff = StringToDiff(splitted[3]);
                LevelBlock block = new LevelBlock(level, BlockCategory.THEME, type, entry, exit, diff);
                addTo.add(block);
            } catch (IOException e) {
                e.printStackTrace();
            }

            theme.put(THEME_SPIKY, spiky);
            theme.put(BlockType.THEME_BULLET, bullet);
            theme.put(BlockType.THEME_JUMP, jump);
            theme.put(BlockType.THEME_PIPE, pipe);
        }
    }

    private void LoadMix() {
        List<LevelBlock> spikyBullet = new LinkedList<>();
        List<LevelBlock> spikyJump = new LinkedList<>();
        List<LevelBlock> spikyPipe = new LinkedList<>();
        List<LevelBlock> bulletJump = new LinkedList<>();
        List<LevelBlock> bulletPipe = new LinkedList<>();
        List<LevelBlock> jumpPipe = new LinkedList<>();

        File[] listOfFiles = new File(folderName + "mix/").listFiles();

        for (File file : listOfFiles
        ) {
            String name = file.getName();
            String[] splitted = name.split("_");
            try {
                List<String> level = Files.readAllLines(file.toPath());
                BlockType type = BlockType.MIX_SPIKY_BULLET;
                List<LevelBlock> addTo = spikyBullet;
                switch (splitted[0]) {
                    case "SJ":
                        type = BlockType.MIX_SPIKY_JUMP;
                        addTo = spikyJump;
                        break;
                    case "SP":
                        type = BlockType.MIX_SPIKY_PIPE;
                        addTo = spikyPipe;
                        break;
                    case "BJ":
                        type = BlockType.MIX_BULLET_JUMP;
                        addTo = bulletJump;
                        break;
                    case "BP":
                        type = BlockType.MIX_BULLET_PIPE;
                        addTo = bulletPipe;
                        break;
                    case "JP":
                        type = BlockType.MIX_JUMP_PIPE;
                        addTo = jumpPipe;
                        break;
                }

                HeightType entry = StringToHeight(splitted[1]);
                HeightType exit = StringToHeight(splitted[2]);
                LevelBlock block = new LevelBlock(level, BlockCategory.MIX, type, entry, exit, null);
                addTo.add(block);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mix.put(BlockType.MIX_SPIKY_BULLET, spikyBullet);
            mix.put(BlockType.MIX_SPIKY_JUMP, spikyJump);
            mix.put(BlockType.MIX_SPIKY_PIPE, spikyPipe);
            mix.put(BlockType.MIX_BULLET_JUMP, bulletJump);
            mix.put(BlockType.MIX_BULLET_PIPE, bulletPipe);
            mix.put(BlockType.MIX_JUMP_PIPE, jumpPipe);
        }
    }


    private HeightType StringToHeight(String string) {
        HeightType height = HeightType.LOW;
        switch (string) {
            case "M":
                height = HeightType.MID;
                break;
            case "H":
                height = HeightType.HIGH;
                break;
        }
        return height;
    }

    private DifficultyType StringToDiff(String string) {
        DifficultyType diff = DifficultyType.INTRO;
        switch (string) {
            case "M":
                diff = DifficultyType.MID;
                break;
            case "H":
                diff = DifficultyType.HARD;
                break;
        }
        return diff;
    }
}

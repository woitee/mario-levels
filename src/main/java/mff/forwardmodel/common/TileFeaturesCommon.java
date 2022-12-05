package mff.forwardmodel.common;

import java.util.ArrayList;

public enum TileFeaturesCommon {
    BLOCK_UPPER,
    BLOCK_ALL,
    BLOCK_LOWER,
    SPECIAL,
    LIFE,
    BUMPABLE,
    BREAKABLE,
    PICKABLE,
    ANIMATED,
    SPAWNER;

    public static ArrayList<TileFeaturesCommon> getTileFeatures(byte levelPartValue) {
        switch (levelPartValue) {
            case 1: //GROUND_BLOCK
            case 2: //PYRAMID_BLOCK:
            case 14: //USED:
            case 18: //PIPE_TOP_LEFT:
            case 19: //PIPE_TOP_RIGHT:
            case 20: //PIPE_BODY_LEFT:
            case 21: //PIPE_BODY_RIGHT:
            case 4: //BULLET_BILL_BASE:
            case 5: //BULLET_BILL_COLUMN:
            case 52: //PIPE_SINGLE_TOP:
            case 53: //PIPE_SINGLE_BODY:
            case 100: //PIPE_TOP_LEFT_WITH_FLOWER:
            case 101: //PIPE_TOP_LEFT_WITHOUT_FLOWER:
                return blockAll;
            case 43: //JUMP_THROUGH_BLOCK_ALONE:
            case 44: //JUMP_THROUGH_BLOCK_LEFT:
            case 45: //JUMP_THROUGH_BLOCK_RIGHT:
            case 46: //JUMP_THROUGH_BLOCK_CENTER:
                return blockLower;
            case 48: //INVISIBLE_HEALTH_UP_BLOCK:
                return blockUpper_life_bumpable;
            case 49: //INVISIBLE_COIN_BLOCK:
                return bumpable_blockUpper;
            case 3: //BULLET_BILL_CANNON:
                return blockAll_spawner;
            case 8: //POWER_UP_QUESTION_BLOCK:
                return blockAll_special_bumpable_animated;
            case 11: //COIN_QUESTION_BLOCK:
                return blockAll_bumpable_animated;
            case 6: //NORMAL_BRICK_BLOCK:
                return blockAll_breakable;
            case 7: //COIN_BRICK_BLOCK:
                return blockAll_bumpable;
            case 15: //COIN:
                return pickable_animated;
            case 50: //POWER_UP_BRICK_BLOCK:
                return blockAll_special_bumpable;
            case 51: //HEALTH_UP_BRICK_BLOCK:
                return blockAll_life_bumpable;
            case 0: //EMPTY:
            default:
                return empty;
        }
    }

    private static final ArrayList<TileFeaturesCommon> blockAll = new ArrayList<>() {{
        add(BLOCK_ALL);
    }};

    private static final ArrayList<TileFeaturesCommon> blockLower = new ArrayList<>() {{
        add(BLOCK_LOWER);
    }};

    private static final ArrayList<TileFeaturesCommon> blockUpper_life_bumpable = new ArrayList<>() {{
        add(BLOCK_UPPER);
        add(LIFE);
        add(BUMPABLE);
    }};

    private static final ArrayList<TileFeaturesCommon> bumpable_blockUpper = new ArrayList<>() {{
        add(BUMPABLE);
        add(BLOCK_UPPER);
    }};

    private static final ArrayList<TileFeaturesCommon> blockAll_spawner = new ArrayList<>() {{
        add(BLOCK_ALL);
        add(SPAWNER);
    }};

    private static final ArrayList<TileFeaturesCommon> blockAll_special_bumpable_animated = new ArrayList<>() {{
        add(BLOCK_ALL);
        add(SPECIAL);
        add(BUMPABLE);
        add(ANIMATED);
    }};

    private static final ArrayList<TileFeaturesCommon> blockAll_bumpable_animated = new ArrayList<>() {{
        add(BLOCK_ALL);
        add(BUMPABLE);
        add(ANIMATED);
    }};

    private static final ArrayList<TileFeaturesCommon> blockAll_breakable = new ArrayList<>() {{
        add(BLOCK_ALL);
        add(BREAKABLE);
    }};

    private static final ArrayList<TileFeaturesCommon> blockAll_bumpable = new ArrayList<>() {{
        add(BLOCK_ALL);
        add(BUMPABLE);
    }};

    private static final ArrayList<TileFeaturesCommon> pickable_animated = new ArrayList<>() {{
        add(PICKABLE);
        add(ANIMATED);
    }};

    private static final ArrayList<TileFeaturesCommon> blockAll_special_bumpable = new ArrayList<>() {{
        add(BLOCK_ALL);
        add(SPECIAL);
        add(BUMPABLE);
    }};

    private static final ArrayList<TileFeaturesCommon> blockAll_life_bumpable = new ArrayList<>() {{
        add(BLOCK_ALL);
        add(LIFE);
        add(BUMPABLE);
    }};

    private static final ArrayList<TileFeaturesCommon> empty = new ArrayList<>();
}

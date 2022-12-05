package mff.forwardmodel.common;

public enum LevelPart {
    // sprites - multiplied by -1 to avoid collisions with tiles
    // only sprites that are a part of the level layout
    GOOMBA((byte) -2),
    GOOMBA_WINGED((byte) -3),
    RED_KOOPA((byte) -4),
    RED_KOOPA_WINGED((byte) -5),
    GREEN_KOOPA((byte) -6),
    GREEN_KOOPA_WINGED((byte) -7),
    SPIKY((byte) -8),
    SPIKY_WINGED((byte) -9),
    ENEMY_FLOWER((byte) -11),

    // special
    PIPE_TOP_LEFT_WITH_FLOWER((byte) 100), // ENEMY_FLOWER is here
    PIPE_TOP_LEFT_WITHOUT_FLOWER((byte) 101), // ENEMY_FLOWER was already spawned

    // tiles
    EMPTY((byte) 0),
    GROUND_BLOCK((byte) 1),
    PYRAMID_BLOCK((byte) 2),
    BULLET_BILL_CANNON((byte) 3),
    BULLET_BILL_BASE((byte) 4),
    BULLET_BILL_COLUMN((byte) 5),
    NORMAL_BRICK_BLOCK((byte) 6),
    COIN_BRICK_BLOCK((byte) 7),
    POWER_UP_QUESTION_BLOCK((byte) 8),
    COIN_QUESTION_BLOCK((byte) 11),
    USED((byte) 14),
    COIN((byte) 15),
    PIPE_TOP_LEFT((byte) 18),
    PIPE_TOP_RIGHT((byte) 19),
    PIPE_BODY_LEFT((byte) 20),
    PIPE_BODY_RIGHT((byte) 21),
    // 39 = flag, 40 = flag pole, not needed
    JUMP_THROUGH_BLOCK_ALONE((byte) 43),
    JUMP_THROUGH_BLOCK_LEFT((byte) 44),
    JUMP_THROUGH_BLOCK_RIGHT((byte) 45),
    JUMP_THROUGH_BLOCK_CENTER((byte) 46),
    JUMP_THROUGH_BLOCK_BACKGROUND((byte) 47),
    INVISIBLE_HEALTH_UP_BLOCK((byte) 48),
    INVISIBLE_COIN_BLOCK((byte) 49),
    POWER_UP_BRICK_BLOCK((byte) 50),
    HEALTH_UP_BRICK_BLOCK((byte) 51),
    PIPE_SINGLE_TOP((byte) 52),
    PIPE_SINGLE_BODY((byte) 53);

    private final byte value;

    LevelPart(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static LevelPart getLevelPart(int value, boolean levelTile) {
        if (value == 18 || value == -11) // assume pipe with flower
            return PIPE_TOP_LEFT_WITH_FLOWER;
        if (!levelTile)
            value *= -1;
        for (LevelPart levelPart : LevelPart.values()) {
            if (levelPart.value == value)
                return levelPart;
        }
        throw new IllegalArgumentException();
    }

    public static byte checkLevelBlock(byte levelPartValue) {
        if (levelPartValue == PIPE_TOP_LEFT_WITH_FLOWER.getValue() ||
            levelPartValue == PIPE_TOP_LEFT_WITHOUT_FLOWER.getValue())
            return PIPE_TOP_LEFT.getValue();
        if (levelPartValue < 0)
            return EMPTY.getValue();
        else
            return levelPartValue;
    }

    public static SpriteTypeCommon getLevelSprite(byte levelPartValue) {
        if (levelPartValue == PIPE_TOP_LEFT_WITH_FLOWER.getValue())
            return SpriteTypeCommon.getSpriteTypeSlim((byte) -ENEMY_FLOWER.value);
        if (levelPartValue == PIPE_TOP_LEFT_WITHOUT_FLOWER.getValue())
            return SpriteTypeCommon.NONE;

        if (levelPartValue >= 0)
            return SpriteTypeCommon.NONE;
        else {
            levelPartValue *= -1;
            return SpriteTypeCommon.getSpriteTypeSlim(levelPartValue);
        }
    }

    public static boolean isDynamic(LevelPart levelPart) {
        switch (levelPart) {
            case GOOMBA:
            case GOOMBA_WINGED:
            case RED_KOOPA:
            case RED_KOOPA_WINGED:
            case GREEN_KOOPA:
            case GREEN_KOOPA_WINGED:
            case SPIKY:
            case SPIKY_WINGED:
            case ENEMY_FLOWER:
            case NORMAL_BRICK_BLOCK:
            case COIN_BRICK_BLOCK:
            case POWER_UP_QUESTION_BLOCK:
            case COIN_QUESTION_BLOCK:
            case COIN:
            case INVISIBLE_HEALTH_UP_BLOCK:
            case INVISIBLE_COIN_BLOCK:
            case POWER_UP_BRICK_BLOCK:
            case HEALTH_UP_BRICK_BLOCK:
            case PIPE_TOP_LEFT_WITH_FLOWER:
                return true;
            case EMPTY:
            case GROUND_BLOCK:
            case PYRAMID_BLOCK:
            case BULLET_BILL_CANNON:
            case BULLET_BILL_BASE:
            case BULLET_BILL_COLUMN:
            case USED:
            case PIPE_TOP_LEFT:
            case PIPE_TOP_RIGHT:
            case PIPE_BODY_LEFT:
            case PIPE_BODY_RIGHT:
            case JUMP_THROUGH_BLOCK_ALONE:
            case JUMP_THROUGH_BLOCK_LEFT:
            case JUMP_THROUGH_BLOCK_RIGHT:
            case JUMP_THROUGH_BLOCK_CENTER:
            case JUMP_THROUGH_BLOCK_BACKGROUND:
            case PIPE_SINGLE_TOP:
            case PIPE_SINGLE_BODY:
            case PIPE_TOP_LEFT_WITHOUT_FLOWER:
                return false;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static LevelPart getUsedState(LevelPart levelPart) {
        switch (levelPart) {
            case GOOMBA:
            case GOOMBA_WINGED:
            case RED_KOOPA:
            case RED_KOOPA_WINGED:
            case GREEN_KOOPA:
            case GREEN_KOOPA_WINGED:
            case SPIKY:
            case SPIKY_WINGED:
            case NORMAL_BRICK_BLOCK:
            case COIN:
                return EMPTY;
            case COIN_BRICK_BLOCK:
            case POWER_UP_QUESTION_BLOCK:
            case COIN_QUESTION_BLOCK:
            case INVISIBLE_HEALTH_UP_BLOCK:
            case INVISIBLE_COIN_BLOCK:
            case POWER_UP_BRICK_BLOCK:
            case HEALTH_UP_BRICK_BLOCK:
                return USED;
            case PIPE_TOP_LEFT_WITH_FLOWER:
                return PIPE_TOP_LEFT_WITHOUT_FLOWER;
            default:
                throw new IllegalArgumentException();
        }
    }
}

package mff.forwardmodel.common;

public enum SpriteTypeCommon {
    NONE(0),
    MARIO(1),
    GOOMBA(2),
    GOOMBA_WINGED(3),
    RED_KOOPA(4),
    RED_KOOPA_WINGED(5),
    GREEN_KOOPA(6),
    GREEN_KOOPA_WINGED(7),
    SPIKY(8),
    SPIKY_WINGED(9),
    BULLET_BILL(10),
    ENEMY_FLOWER(11),
    MUSHROOM(12),
    FIRE_FLOWER(13),
    SHELL(14),
    LIFE_MUSHROOM(15),
    FIREBALL(16);

    private final int value;

    SpriteTypeCommon(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SpriteTypeCommon getSpriteTypeSlim(int value) {
        for (SpriteTypeCommon spriteTypeCommon : SpriteTypeCommon.values()) {
            if (spriteTypeCommon.value == value)
                return spriteTypeCommon;
        }
        throw new IllegalArgumentException();
    }
}

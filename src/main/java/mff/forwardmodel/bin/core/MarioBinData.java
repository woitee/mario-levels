package mff.forwardmodel.bin.core;

import mff.forwardmodel.bin.level.MarioLevelBin;
import mff.forwardmodel.common.SpriteTypeCommon;
import mff.forwardmodel.common.StaticLevel;
import mff.forwardmodel.slim.core.MarioSpriteSlim;
import mff.forwardmodel.slim.core.MarioWorldSlim;
import mff.forwardmodel.common.LevelPart;
import mff.forwardmodel.slim.level.MarioLevelSlim;
import mff.forwardmodel.slim.sprites.*;

public class MarioBinData {
    private static final short SPRITE_STORAGE_INFO_SIZE = 27;

    // sprite bool counts
    public static final int BULLET_BILL_BOOLS = 1;
    public static final int ENEMY_BOOLS = 5;
    public static final int FIREBALL_BOOLS = 2;  // bin model special "isValid" field
    public static final int FIRE_FLOWER_BOOLS = 2;
    public static final int FLOWER_ENEMY_BOOLS = 1;
    public static final int LIFE_MUSHROOM_BOOLS = 2;
    public static final int MARIO_BOOLS = 10;
    public static final int MUSHROOM_BOOLS = 2;
    public static final int SHELL_BOOLS = 2;

    // sprite, world, level int counts
    public static final int WORLD_INTS = 6;
    public static final int LEVEL_INTS = 8;
    public static final int BULLET_BILL_INTS = 1;
    public static final int ENEMY_INTS = 3;
    public static final int FIREBALL_INTS = 1;
    public static final int FIRE_FLOWER_INTS = 1;
    public static final int FLOWER_ENEMY_INTS = 1;
    public static final int LIFE_MUSHROOM_INTS = 2;
    public static final int MARIO_INTS = 4;
    public static final int MUSHROOM_INTS = 2;
    public static final int SHELL_INTS = 1;

    // sprite, world, level float counts
    public static final int WORLD_FLOATS = 2;
    public static final int BULLET_BILL_FLOATS = 2;
    public static final int ENEMY_FLOATS = 4;
    public static final int FIREBALL_FLOATS = 4;
    public static final int FIRE_FLOWER_FLOATS = 2;
    public static final int FLOWER_ENEMY_FLOATS = 4;
    public static final int LIFE_MUSHROOM_FLOATS = 4;
    public static final int MARIO_FLOATS = 7;
    public static final int MUSHROOM_FLOATS = 4;
    public static final int SHELL_FLOATS = 4;

    // world and level int layout
    public static final int PAUSE_TIMER = 0;
    public static final int CURRENT_TIMER = 1;
    public static final int CURRENT_TICK = 2;
    public static final int COINS = 3;
    public static final int LIVES = 4;
    public static final int GAME_STATUS_CODE = 5;

    public static final int WIDTH = 6;
    public static final int TILE_WIDTH = 7;
    public static final int HEIGHT = 8;
    public static final int TILE_HEIGHT = 9;
    public static final int EXIT_TILE_X = 10;
    public static final int CURRENT_CUTOUT_CENTER = 11;
    public static final int CUTOUT_ARRAY_BEGINNING_INDEX = 12;
    public static final int CUTOUT_LEFT_BORDER_X = 13;

    // world float layout
    public static final int CAMERA_X = 0;
    public static final int CAMERA_Y = 1;

    private static final int MARIO_COUNT = 1;
    private static final int FIREBALLS_MAX_COUNT = 2; // there can't be more than 2 fireballs at a time

    public static final int SPRITES_FIRST_FREE_INDEX = 0;

    // sprite storage info layout
    public static final int BOOLS_MARIO_START = 0;
    public static final int BOOLS_FIREBALL_START = 1;
    public static final int BOOLS_ENEMY_START = 2;
    public static final int BOOLS_FIRE_FLOWER_START = 3;
    public static final int BOOLS_FLOWER_ENEMY_START = 4;
    public static final int BOOLS_LIFE_MUSHROOM_START = 5;
    public static final int BOOLS_MUSHROOM_START = 6;
    public static final int BOOLS_SHELL_START = 7;
    public static final int BOOLS_BULLET_BILL_START = 8;

    public static final int INTS_MARIO_START = 9;
    public static final int INTS_FIREBALL_START = 10;
    public static final int INTS_ENEMY_START = 11;
    public static final int INTS_FIRE_FLOWER_START = 12;
    public static final int INTS_FLOWER_ENEMY_START = 13;
    public static final int INTS_LIFE_MUSHROOM_START = 14;
    public static final int INTS_MUSHROOM_START = 15;
    public static final int INTS_SHELL_START = 16;
    public static final int INTS_BULLET_BILL_START = 17;

    public static final int FLOATS_MARIO_START = 18;
    public static final int FLOATS_FIREBALL_START = 19;
    public static final int FLOATS_ENEMY_START = 20;
    public static final int FLOATS_FIRE_FLOWER_START = 21;
    public static final int FLOATS_FLOWER_ENEMY_START = 22;
    public static final int FLOATS_LIFE_MUSHROOM_START = 23;
    public static final int FLOATS_MUSHROOM_START = 24;
    public static final int FLOATS_SHELL_START = 25;
    public static final int FLOATS_BULLET_BILL_START = 26;

    // TODO: describe sprite order

    /**
     * BOOLS LAYOUT
     * - ALIVE FLAGS
     * - SPRITE INFO
     *
     * BYTES LAYOUT
     * - LEVEL CUTOUT
     *
     * INTS LAYOUT
     *  WORLD
     * 0 = pauseTimer
     * 1 = currentTimer
     * 2 = currentTick
     * 3 = coins
     * 4 = lives
     * 5 = gameStatusCode
     *  LEVEL
     * 6 = width
     * 7 = tileWidth
     * 8 = height
     * 9 = tileHeight
     * 10 = exitTileX
     * 11 = currentCutoutCenter
     * 12 = cutoutArrayBeginningIndex
     * 13 = cutoutLeftBorderX
     *  SPRITE INFO
     *  - 14+
     *
     * FLOATS LAYOUT
     *  WORLD
     * 0 = cameraX
     * 1 = cameraY
     *  SPRITE INFO
     * - 2+
     */

    public boolean[] bools;
    public byte[] bytes;
    public short[] spriteStorageInfo;
    public int[] ints;
    public int[] sprites;
    public float[] floats;
    public StaticLevel staticLevel;

    public MarioBinData(MarioWorldSlim slimWorld) {
        staticLevel = slimWorld.level.staticLevel;
        MarioLevelBin.cutoutTileWidth = MarioLevelSlim.cutoutTileWidth;

        int bulletBillCounter = 0;
        int enemyCounter = 0;
        int fireFlowerCounter = 0;
        int flowerEnemyCounter = 0;
        int lifeMushroomCounter = 0;
        int mushroomCounter = 0;
        int shellCounter = 0;

        for (MarioSpriteSlim spriteSlim : slimWorld.sprites) {
            if (spriteSlim instanceof EnemySlim) {
                enemyCounter++;
                if (spriteSlim.getType() == SpriteTypeCommon.RED_KOOPA ||
                    spriteSlim.getType() == SpriteTypeCommon.RED_KOOPA_WINGED ||
                    spriteSlim.getType() == SpriteTypeCommon.GREEN_KOOPA ||
                    spriteSlim.getType() == SpriteTypeCommon.GREEN_KOOPA_WINGED)
                    shellCounter++;
            }
            else if (spriteSlim instanceof FireFlowerSlim)
                fireFlowerCounter++;
            else if (spriteSlim instanceof FlowerEnemySlim)
                flowerEnemyCounter++;
            else if (spriteSlim instanceof LifeMushroomSlim)
                lifeMushroomCounter++;
            else if (spriteSlim instanceof MushroomSlim)
                mushroomCounter++;
            else if (spriteSlim instanceof ShellSlim)
                shellCounter++;
        }

        for (int x = 0; x < slimWorld.level.staticLevel.tiles.length; x++) {
            for (int y = 0; y < slimWorld.level.staticLevel.tiles[0].length; y++) {
                StaticLevel.LevelTile levelTile = slimWorld.level.staticLevel.tiles[x][y];
                LevelPart levelPart = levelTile.levelPart;
                int id = levelTile.id;

                if (levelPart == LevelPart.BULLET_BILL_CANNON)
                    bulletBillCounter++; // each cannon should have max one bill spawned at a time
                else if (id != -1 && slimWorld.level.aliveFlags[id]) {
                    if (levelPart == LevelPart.GOOMBA || levelPart == LevelPart.GOOMBA_WINGED ||
                        levelPart == LevelPart.RED_KOOPA || levelPart == LevelPart.RED_KOOPA_WINGED ||
                        levelPart == LevelPart.GREEN_KOOPA || levelPart == LevelPart.GREEN_KOOPA_WINGED ||
                        levelPart == LevelPart.SPIKY || levelPart == LevelPart.SPIKY_WINGED) {
                        enemyCounter++;
                        if (levelPart == LevelPart.RED_KOOPA || levelPart == LevelPart.RED_KOOPA_WINGED ||
                            levelPart == LevelPart.GREEN_KOOPA || levelPart == LevelPart.GREEN_KOOPA_WINGED)
                            shellCounter++;
                    }
                    else if (levelPart == LevelPart.POWER_UP_QUESTION_BLOCK || levelPart == LevelPart.POWER_UP_BRICK_BLOCK) {
                        fireFlowerCounter++;
                        mushroomCounter++;
                    }
                    else if (levelPart == LevelPart.PIPE_TOP_LEFT_WITH_FLOWER)
                        flowerEnemyCounter++;
                    else if (levelPart == LevelPart.INVISIBLE_HEALTH_UP_BLOCK || levelPart == LevelPart.HEALTH_UP_BRICK_BLOCK)
                        lifeMushroomCounter++;
                }
            }
        }

        int initBoolsSize = slimWorld.level.aliveFlags.length;

        int initBytesSize = slimWorld.level.levelCutout.length;

        int totalSpriteCount = bulletBillCounter + enemyCounter + FIREBALLS_MAX_COUNT +
                fireFlowerCounter + flowerEnemyCounter + lifeMushroomCounter +
                MARIO_COUNT + mushroomCounter + shellCounter;

        int initIntsSize = WORLD_INTS + LEVEL_INTS;

        int initFloatsSize = WORLD_FLOATS;

        bools = new boolean[initBoolsSize];
        bytes = new byte[initBytesSize];
        spriteStorageInfo = new short[SPRITE_STORAGE_INFO_SIZE];
        ints = new int[initIntsSize];
        sprites = new int[totalSpriteCount + 1]; // index 0 is first free index
        floats = new float[initFloatsSize];

        /* SPRITE STORAGE INFO */
        /* BOOLS */
        spriteStorageInfo[BOOLS_MARIO_START] = (short) slimWorld.level.aliveFlags.length;
        spriteStorageInfo[BOOLS_FIREBALL_START] = (short) (spriteStorageInfo[BOOLS_MARIO_START] + MARIO_COUNT * MARIO_BOOLS);
        spriteStorageInfo[BOOLS_ENEMY_START] = (short) (spriteStorageInfo[BOOLS_FIREBALL_START] + FIREBALLS_MAX_COUNT * FIREBALL_BOOLS);
        spriteStorageInfo[BOOLS_FIRE_FLOWER_START] = (short) (spriteStorageInfo[BOOLS_ENEMY_START] + enemyCounter * ENEMY_BOOLS);
        spriteStorageInfo[BOOLS_FLOWER_ENEMY_START] = (short) (spriteStorageInfo[BOOLS_FIRE_FLOWER_START] + fireFlowerCounter * FIRE_FLOWER_BOOLS);
        spriteStorageInfo[BOOLS_LIFE_MUSHROOM_START] = (short) (spriteStorageInfo[BOOLS_FLOWER_ENEMY_START] + flowerEnemyCounter * FLOWER_ENEMY_BOOLS);
        spriteStorageInfo[BOOLS_MUSHROOM_START] = (short) (spriteStorageInfo[BOOLS_LIFE_MUSHROOM_START] + lifeMushroomCounter * LIFE_MUSHROOM_BOOLS);
        spriteStorageInfo[BOOLS_SHELL_START] = (short) (spriteStorageInfo[BOOLS_MUSHROOM_START] + mushroomCounter * MUSHROOM_BOOLS);
        spriteStorageInfo[BOOLS_BULLET_BILL_START] = (short) (spriteStorageInfo[BOOLS_SHELL_START] + shellCounter * SHELL_BOOLS);
        /* INTS */
        spriteStorageInfo[INTS_MARIO_START] = (short) (WORLD_INTS + LEVEL_INTS);
        spriteStorageInfo[INTS_FIREBALL_START] = (short) (spriteStorageInfo[INTS_MARIO_START] + MARIO_COUNT * MARIO_INTS);
        spriteStorageInfo[INTS_ENEMY_START] = (short) (spriteStorageInfo[INTS_FIREBALL_START] + FIREBALLS_MAX_COUNT * FIREBALL_INTS);
        spriteStorageInfo[INTS_FIRE_FLOWER_START] = (short) (spriteStorageInfo[INTS_ENEMY_START] + enemyCounter * ENEMY_INTS);
        spriteStorageInfo[INTS_FLOWER_ENEMY_START] = (short) (spriteStorageInfo[INTS_FIRE_FLOWER_START] + fireFlowerCounter * FIRE_FLOWER_INTS);
        spriteStorageInfo[INTS_LIFE_MUSHROOM_START] = (short) (spriteStorageInfo[INTS_FLOWER_ENEMY_START] + flowerEnemyCounter * FLOWER_ENEMY_INTS);
        spriteStorageInfo[INTS_MUSHROOM_START] = (short) (spriteStorageInfo[INTS_LIFE_MUSHROOM_START] + lifeMushroomCounter * LIFE_MUSHROOM_INTS);
        spriteStorageInfo[INTS_SHELL_START] = (short) (spriteStorageInfo[INTS_MUSHROOM_START] + mushroomCounter * MUSHROOM_INTS);
        spriteStorageInfo[INTS_BULLET_BILL_START] = (short) (spriteStorageInfo[INTS_SHELL_START] + shellCounter * SHELL_INTS);
        /* FLOATS */
        spriteStorageInfo[FLOATS_MARIO_START] = WORLD_FLOATS;
        spriteStorageInfo[FLOATS_FIREBALL_START] = (short) (spriteStorageInfo[FLOATS_MARIO_START] + MARIO_COUNT * MARIO_FLOATS);
        spriteStorageInfo[FLOATS_ENEMY_START] = (short) (spriteStorageInfo[FLOATS_FIREBALL_START] + FIREBALLS_MAX_COUNT * FIREBALL_FLOATS);
        spriteStorageInfo[FLOATS_FIRE_FLOWER_START] = (short) (spriteStorageInfo[FLOATS_ENEMY_START] + enemyCounter * ENEMY_FLOATS);
        spriteStorageInfo[FLOATS_FLOWER_ENEMY_START] = (short) (spriteStorageInfo[FLOATS_FIRE_FLOWER_START] + fireFlowerCounter * FIRE_FLOWER_FLOATS);
        spriteStorageInfo[FLOATS_LIFE_MUSHROOM_START] = (short) (spriteStorageInfo[FLOATS_FLOWER_ENEMY_START] + flowerEnemyCounter * FLOWER_ENEMY_FLOATS);
        spriteStorageInfo[FLOATS_MUSHROOM_START] = (short) (spriteStorageInfo[FLOATS_LIFE_MUSHROOM_START] + lifeMushroomCounter * LIFE_MUSHROOM_FLOATS);
        spriteStorageInfo[FLOATS_SHELL_START] = (short) (spriteStorageInfo[FLOATS_MUSHROOM_START] + mushroomCounter * MUSHROOM_FLOATS);
        spriteStorageInfo[FLOATS_BULLET_BILL_START] = (short) (spriteStorageInfo[FLOATS_SHELL_START] + shellCounter * SHELL_FLOATS);

        /* BOOLS */
        System.arraycopy(slimWorld.level.aliveFlags, 0, bools, 0, slimWorld.level.aliveFlags.length);

        /* BYTES */
        System.arraycopy(slimWorld.level.levelCutout, 0, bytes, 0, slimWorld.level.levelCutout.length);

        /* INTS */
        /* WORLD */
        ints[PAUSE_TIMER] = slimWorld.pauseTimer;
        ints[CURRENT_TIMER] = slimWorld.currentTimer;
        ints[CURRENT_TICK] = slimWorld.currentTick;
        ints[COINS] = slimWorld.coins;
        ints[LIVES] = slimWorld.lives;
        ints[GAME_STATUS_CODE] = slimWorld.gameStatusCode;
        /* LEVEL */
        ints[WIDTH] = slimWorld.level.width;
        ints[TILE_WIDTH] = slimWorld.level.tileWidth;
        ints[HEIGHT] = slimWorld.level.height;
        ints[TILE_HEIGHT] = slimWorld.level.tileHeight;
        ints[EXIT_TILE_X] = slimWorld.level.exitTileX;
        ints[CURRENT_CUTOUT_CENTER] = slimWorld.level.currentCutoutCenter;
        ints[CUTOUT_ARRAY_BEGINNING_INDEX] = slimWorld.level.cutoutArrayBeginningIndex;
        ints[CUTOUT_LEFT_BORDER_X] = slimWorld.level.cutoutLeftBorderX;

        /* SPRITES */
        sprites[SPRITES_FIRST_FREE_INDEX] = 1;

        /* FLOATS */
        /* WORLD */
        floats[CAMERA_X] = slimWorld.cameraX;
        floats[CAMERA_Y] = slimWorld.cameraY;

        /* SPRITES */
        for (MarioSpriteSlim spriteSlim : slimWorld.sprites) {
            if (spriteSlim.getType() == SpriteTypeCommon.MARIO) {
                //addMario((MarioSlim) spriteSlim);
            }
            else if (spriteSlim instanceof EnemySlim) {
                EnemySlim enemySlim = (EnemySlim) spriteSlim;
                /*addEnemy(enemySlim.x, enemySlim.y, enemySlim.alive, enemySlim.typeCode, enemySlim.xa,
                        enemySlim.ya, enemySlim.facing, enemySlim.height, enemySlim.onGround,
                        enemySlim.avoidCliffs, enemySlim.winged, enemySlim.noFireballDeath);*/
            }
            else if (spriteSlim.getType() == SpriteTypeCommon.BULLET_BILL) {
                BulletBillSlim bulletBillSlim = (BulletBillSlim) spriteSlim;
                // TODO better naming?
                addSpriteCode(addBulletBill(bulletBillSlim.x, bulletBillSlim.y, bulletBillSlim.alive, bulletBillSlim.facing));
            }
            else {
                // TODO: rest of the sprites
            }
        }

        // TODO: only for testing
        /*boolsCloning = new boolean[this.bools.length];
        bytesCloning = new byte[this.bytes.length];
        shortsCloning = new short[this.spriteStorageInfo.length];
        intsCloning = new int[this.ints.length];
        floatsCloning = new float[this.floats.length];*/
    }

    public void addSpriteCode(int toAdd) {
        sprites[sprites[SPRITES_FIRST_FREE_INDEX]] = toAdd;
        sprites[SPRITES_FIRST_FREE_INDEX]++;
    }

    private void changeSpriteCode(int oldCode, int newCode) {
        for (int i = 1; i < sprites[SPRITES_FIRST_FREE_INDEX]; i++) {
            if (sprites[i] == oldCode) {
                sprites[i] = newCode;
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    private void removeSpriteCode(int toDelete) {
        for (int i = 1; i < sprites[SPRITES_FIRST_FREE_INDEX]; i++) {
            if (sprites[i] == toDelete) {
                for (int j = i; j < sprites[SPRITES_FIRST_FREE_INDEX] - 2; j++) {
                    sprites[j] = sprites[j + 1];
                }
                sprites[SPRITES_FIRST_FREE_INDEX]--;
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    // TODO: methods - addEnemy etc. return type+id as int
    // TODO: asserts for validity

    /**
     Bullet Bill data storage order:
     BOOL:
      0 = alive
     INT:
      0 = facing
     FLOAT:
      0 = x
      1 = y
     */

    public static final int BULLET_BILL_ALIVE = 0;

    public static final int BULLET_BILL_FACING = 0;

    public static final int BULLET_BILL_X = 0;
    public static final int BULLET_BILL_Y = 1;

    //TODO add data immediately, alive and update in world.add, code to sprites at the end of world.update
    int addBulletBill(float x, float y, boolean alive, int facing) {
        int id = (bools.length - spriteStorageInfo[BOOLS_BULLET_BILL_START]) / BULLET_BILL_BOOLS;

        boolean[] newBools = new boolean[bools.length + BULLET_BILL_BOOLS];
        System.arraycopy(bools, 0, newBools, 0, bools.length);
        newBools[spriteStorageInfo[BOOLS_BULLET_BILL_START] + id * BULLET_BILL_BOOLS + BULLET_BILL_ALIVE] = alive;
        bools = newBools;

        int[] newInts = new int[ints.length + BULLET_BILL_INTS];
        System.arraycopy(ints, 0, newInts, 0, ints.length);
        newInts[spriteStorageInfo[INTS_BULLET_BILL_START] + id * BULLET_BILL_INTS + BULLET_BILL_FACING] = facing;
        ints = newInts;

        float[] newFloats = new float[floats.length + BULLET_BILL_FLOATS];
        System.arraycopy(floats, 0, newFloats, 0, floats.length);
        newFloats[spriteStorageInfo[FLOATS_BULLET_BILL_START] + id * BULLET_BILL_FLOATS + BULLET_BILL_X] = x;
        newFloats[spriteStorageInfo[FLOATS_BULLET_BILL_START] + id * BULLET_BILL_FLOATS + BULLET_BILL_Y] = y;
        floats = newFloats;

        int spriteCode = id << 16;
        spriteCode |= SpriteTypeCommon.BULLET_BILL.getValue();

        return spriteCode;
        //addSpriteCode(spriteCode);
    }

    //TODO alive false immediately, remove from data and sprites at the end of world.update
    public void removeBulletBill(int entityIndex) {
        int bulletBillsCount = (bools.length - spriteStorageInfo[BOOLS_BULLET_BILL_START]) / BULLET_BILL_BOOLS;

        /*
        // move codes in sprites if needed
        if (entityIndex + 1 != bulletBillsCount) {
            for (int i = entityIndex + 1; i < bulletBillsCount; i++) {
                int oldSpriteCode = (i - 1) << 16;
                oldSpriteCode |= SpriteTypeCommon.BULLET_BILL.getValue();

                int newSpriteCode = i << 16;
                newSpriteCode |= SpriteTypeCommon.BULLET_BILL.getValue();

                changeSpriteCode(oldSpriteCode, newSpriteCode);
            }
            int lastSpriteCode = (bulletBillsCount - 1) << 16;
            lastSpriteCode |= SpriteTypeCommon.BULLET_BILL.getValue();
            removeSpriteCode(lastSpriteCode);
        }
        */

        // remove last bill id from sprites - data shifted
        int lastSpriteCode = (bulletBillsCount - 1) << 16;
        lastSpriteCode |= SpriteTypeCommon.BULLET_BILL.getValue();
        removeSpriteCode(lastSpriteCode);

        boolean[] newBools = new boolean[bools.length - BULLET_BILL_BOOLS];
        System.arraycopy(bools, 0, newBools, 0, spriteStorageInfo[BOOLS_BULLET_BILL_START]
                + entityIndex * BULLET_BILL_BOOLS);
        System.arraycopy(bools, spriteStorageInfo[BOOLS_BULLET_BILL_START] + (entityIndex + 1) * BULLET_BILL_BOOLS,
                newBools, spriteStorageInfo[BOOLS_BULLET_BILL_START] + entityIndex * BULLET_BILL_BOOLS,
                bools.length - (spriteStorageInfo[BOOLS_BULLET_BILL_START] + (entityIndex + 1) * BULLET_BILL_BOOLS));
        bools = newBools;

        int[] newInts = new int[ints.length - BULLET_BILL_INTS];
        System.arraycopy(ints, 0, newInts, 0, spriteStorageInfo[INTS_BULLET_BILL_START]
                + entityIndex * BULLET_BILL_INTS);
        System.arraycopy(ints, spriteStorageInfo[INTS_BULLET_BILL_START] + (entityIndex + 1) * BULLET_BILL_INTS,
                newInts, spriteStorageInfo[INTS_BULLET_BILL_START] + entityIndex * BULLET_BILL_INTS,
                ints.length - (spriteStorageInfo[INTS_BULLET_BILL_START] + (entityIndex + 1) * BULLET_BILL_INTS));
        ints = newInts;

        float[] newFloats = new float[floats.length - BULLET_BILL_FLOATS];
        System.arraycopy(floats, 0, newFloats, 0, spriteStorageInfo[FLOATS_BULLET_BILL_START]
                + entityIndex * BULLET_BILL_FLOATS);
        System.arraycopy(floats, spriteStorageInfo[FLOATS_BULLET_BILL_START] + (entityIndex + 1) * BULLET_BILL_FLOATS,
                newFloats, spriteStorageInfo[FLOATS_BULLET_BILL_START] + entityIndex * BULLET_BILL_FLOATS,
                floats.length - (spriteStorageInfo[FLOATS_BULLET_BILL_START] + (entityIndex + 1) * BULLET_BILL_FLOATS));
        floats = newFloats;
    }

    /**
     Enemy data storage order:
     BOOL:
     0 = alive
     1 = onGround
     2 = avoidCliffs
     3 = winged
     4 = noFireballDeath
     INT:
     0 = typeCode
     1 = facing
     2 = height
     FLOAT:
     0 = x
     1 = y
     2 = xa
     3 = ya
    */

    public static final int ENEMY_ALIVE = 0;
    public static final int ENEMY_ON_GROUND = 1;
    public static final int ENEMY_AVOID_CLIFFS = 2;
    public static final int ENEMY_WINGED = 3;
    public static final int ENEMY_NO_FIREBALL_DEATH = 4;

    public static final int ENEMY_TYPE_CODE = 0;
    public static final int ENEMY_FACING = 1;
    public static final int ENEMY_HEIGHT = 2;

    public static final int ENEMY_X = 0;
    public static final int ENEMY_Y = 1;
    public static final int ENEMY_XA = 2;
    public static final int ENEMY_YA = 3;

    int addEnemy(float x, float y, boolean alive, int typeCode, float xa, float ya, int facing, int height,
                 boolean onGround, boolean avoidCliffs, boolean winged, boolean noFireballDeath) {
        /*int id = ints[spriteStorageInfo[ENEMY_COUNT]];
        spriteStorageInfo[ENEMY_COUNT]++;
        bools[spriteStorageInfo[BOOLS_ENEMY_START] + id * ENEMY_BOOLS + ENEMY_ALIVE] = alive;
        bools[spriteStorageInfo[BOOLS_ENEMY_START] + id * ENEMY_BOOLS + ENEMY_ON_GROUND] = onGround;
        bools[spriteStorageInfo[BOOLS_ENEMY_START] + id * ENEMY_BOOLS + ENEMY_AVOID_CLIFFS] = avoidCliffs;
        bools[spriteStorageInfo[BOOLS_ENEMY_START] + id * ENEMY_BOOLS + ENEMY_WINGED] = winged;
        bools[spriteStorageInfo[BOOLS_ENEMY_START] + id * ENEMY_BOOLS + ENEMY_NO_FIREBALL_DEATH] = noFireballDeath;

        ints[spriteStorageInfo[INTS_ENEMY_START] + id * ENEMY_INTS + ENEMY_TYPE_CODE] = typeCode;
        ints[spriteStorageInfo[INTS_ENEMY_START] + id * ENEMY_INTS + ENEMY_FACING] = facing;
        ints[spriteStorageInfo[INTS_ENEMY_START] + id * ENEMY_INTS + ENEMY_HEIGHT] = height;

        floats[spriteStorageInfo[FLOATS_ENEMY_START] + id * ENEMY_FLOATS + ENEMY_X] = x;
        floats[spriteStorageInfo[FLOATS_ENEMY_START] + id * ENEMY_FLOATS + ENEMY_Y] = y;
        floats[spriteStorageInfo[FLOATS_ENEMY_START] + id * ENEMY_FLOATS + ENEMY_XA] = xa;
        floats[spriteStorageInfo[FLOATS_ENEMY_START] + id * ENEMY_FLOATS + ENEMY_YA] = ya;

        int spriteCode = id << 16;
        spriteCode |= typeCode;

        return spriteCode;*/
        return 0; //TODO
    }

    void removeEnemy(int entityIndex) {
        //TODO
    }

    public static final int FIREBALL_ALIVE = 0;

    int addFireball(float x, float y, boolean alive, float xa, float ya, int facing, boolean onGround, boolean exists) {
        //TODO
        return 0;
    }

    void removeFireball(int entityIndex) {
        //TODO
    }

    int addFireFlower(float x, float y, boolean alive, int life) {
        //TODO
        return 0;
    }

    void removeFireFlower(int entityIndex) {
        //TODO
    }

    int addFlowerEnemy(float x, float y, boolean alive, int waitTime, float yStart, float ya) {
        //TODO
        return 0;
    }

    void removeFlowerEnemy(int entityIndex) {
        //TODO
    }

    int addLifeMushroom(float x, float y, boolean alive, int facing, int life, boolean onGround, float xa, float ya) {
        //TODO
        return 0;
    }

    void removeLifeMushroom(int entityIndex) {
        //TODO
    }

    /**
     Mario data storage order:
     BOOL:
     0 = alive
     1 = onGround
     2 = wasOnGround
     3 = isLarge
     4 = isDucking
     5 = mayJump
     6 = canShoot
     7 = isFire
     8 = oldLarge
     9 = oldFire
     INT:
     0 = height
     1 = invulnerableTime
     2 = facing
     3 = jumpTime
     FLOAT:
     0 = x
     1 = y
     2 = xa
     3 = ya
     4 = xJumpSpeed
     5 = yJumpSpeed
     6 = xJumpStart
     */

    public static final int MARIO_ALIVE = 0;
    public static final int MARIO_ON_GROUND = 1;
    public static final int MARIO_WAS_ON_GROUND = 2;
    public static final int MARIO_IS_LARGE = 3;
    public static final int MARIO_IS_DUCKING = 4;
    public static final int MARIO_MAY_JUMP = 5;
    public static final int MARIO_CAN_SHOOT = 6;
    public static final int MARIO_IS_FIRE = 7;
    public static final int MARIO_OLD_LARGE = 8;
    public static final int MARIO_OLD_FIRE = 9;

    public static final int MARIO_HEIGHT = 0;
    public static final int MARIO_INVULNERABLE_TIME = 1;
    public static final int MARIO_FACING = 2;
    public static final int MARIO_JUMP_TIME = 3;

    public static final int MARIO_X = 0;
    public static final int MARIO_Y = 1;
    public static final int MARIO_XA = 2;
    public static final int MARIO_YA = 3;
    public static final int MARIO_X_JUMP_SPEED = 4;
    public static final int MARIO_Y_JUMP_SPEED = 5;
    public static final int MARIO_X_JUMP_START = 6;

    int addMario(MarioSlim marioSlim) { // only created once
        /*spriteStorageInfo[MARIO_COUNT]++; // TODO: assert count == 0
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_ALIVE] = marioSlim.alive;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_ON_GROUND] = marioSlim.onGround;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_WAS_ON_GROUND] = marioSlim.wasOnGround;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_IS_LARGE] = marioSlim.isLarge;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_IS_DUCKING] = marioSlim.isDucking;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_MAY_JUMP] = marioSlim.mayJump;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_CAN_SHOOT] = marioSlim.canShoot;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_IS_FIRE] = marioSlim.isFire;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_OLD_LARGE] = marioSlim.oldLarge;
        bools[spriteStorageInfo[BOOLS_MARIO_START] + MARIO_OLD_FIRE] = marioSlim.oldFire;

        ints[spriteStorageInfo[INTS_MARIO_START] + MARIO_HEIGHT] = marioSlim.height;
        ints[spriteStorageInfo[INTS_MARIO_START] + MARIO_INVULNERABLE_TIME] = marioSlim.invulnerableTime;
        ints[spriteStorageInfo[INTS_MARIO_START] + MARIO_FACING] = marioSlim.facing;
        ints[spriteStorageInfo[INTS_MARIO_START] + MARIO_JUMP_TIME] = marioSlim.jumpTime;

        floats[spriteStorageInfo[FLOATS_MARIO_START] + MARIO_X] = marioSlim.x;
        floats[spriteStorageInfo[FLOATS_MARIO_START] + MARIO_Y] = marioSlim.y;
        floats[spriteStorageInfo[FLOATS_MARIO_START] + MARIO_XA] = marioSlim.xa;
        floats[spriteStorageInfo[FLOATS_MARIO_START] + MARIO_YA] = marioSlim.ya;
        floats[spriteStorageInfo[FLOATS_MARIO_START] + MARIO_X_JUMP_SPEED] = marioSlim.xJumpSpeed;
        floats[spriteStorageInfo[FLOATS_MARIO_START] + MARIO_Y_JUMP_SPEED] = marioSlim.yJumpSpeed;
        floats[spriteStorageInfo[FLOATS_MARIO_START] + MARIO_X_JUMP_START] = marioSlim.xJumpStart;

        return SpriteTypeCommon.MARIO.getValue();*/
        return 0; //TODO
    }

    void removeMario() {
        //TODO might be ignored since the game ends here? (probably not)
    }

    int addMushroom(float x, float y, boolean alive, int facing, int life, boolean onGround, float xa, float ya) {
        //TODO
        return 0;
    }

    void removeMushroom(int entityIndex) {
        //TODO
    }

    public static final int SHELL_ALIVE = 0;

    int addShell(float x, float y, boolean alive, int facing, boolean onGround, float xa, float ya) {
        //TODO
        return 0;
    }

    void removeShell(int entityIndex) {
        //TODO
    }

    private MarioBinData() { }

    // TODO: array pooling?, different array copy?
    public MarioBinData clone() {
        MarioBinData clone = new MarioBinData();

        clone.staticLevel = this.staticLevel;

        clone.bools = new boolean[this.bools.length];
        //clone.bools = boolsCloning;
        //clone.bools = getBools();
        System.arraycopy(this.bools, 0, clone.bools, 0, this.bools.length);

        clone.bytes = new byte[this.bytes.length];
        //clone.bytes = bytesCloning;
        //clone.bytes = getBytes();
        System.arraycopy(this.bytes, 0, clone.bytes, 0, this.bytes.length);

        clone.spriteStorageInfo = new short[this.spriteStorageInfo.length];
        //clone.spriteStorageInfo = shortsCloning;
        //clone.spriteStorageInfo = getShorts();
        System.arraycopy(this.spriteStorageInfo, 0, clone.spriteStorageInfo, 0, this.spriteStorageInfo.length);

        clone.ints = new int[this.ints.length];
        //clone.ints = intsCloning;
        //clone.ints = getInts();
        System.arraycopy(this.ints, 0, clone.ints, 0, this.ints.length);

        clone.floats = new float[this.floats.length];
        //clone.floats = floatsCloning;
        //clone.floats = getFloats();
        System.arraycopy(this.floats, 0, clone.floats, 0, this.floats.length);

        return clone;
    }

    // pooling test
    /*
    public void returnArrays() {
        boolsPool.add(bools);
        bytesPool.add(bytes);
        shortsPool.add(spriteStorageInfo);
        intsPool.add(ints);
        floatsPool.add(floats);
    }

    private static ConcurrentLinkedQueue<boolean[]> boolsPool = new ConcurrentLinkedQueue<>();
    private boolean[] getBools() {
        boolean[] bools = boolsPool.poll();
        if (bools != null) return bools;
        return new boolean[this.bools.length];
    }

    private static ConcurrentLinkedQueue<byte[]> bytesPool = new ConcurrentLinkedQueue<>();
    private byte[] getBytes() {
        byte[] bytes = bytesPool.poll();
        if (bytes != null) return bytes;
        return new byte[this.bytes.length];
    }

    private static ConcurrentLinkedQueue<short[]> shortsPool = new ConcurrentLinkedQueue<>();
    private short[] getShorts() {
        short[] shorts = shortsPool.poll();
        if (shorts != null) return shorts;
        return new short[this.spriteStorageInfo.length];
    }

    private static ConcurrentLinkedQueue<int[]> intsPool = new ConcurrentLinkedQueue<>();
    private int[] getInts() {
        int[] ints = intsPool.poll();
        if (ints != null) return ints;
        return new int[this.ints.length];
    }

    private static ConcurrentLinkedQueue<float[]> floatsPool = new ConcurrentLinkedQueue<>();
    private float[] getFloats() {
        float[] floats = floatsPool.poll();
        if (floats != null) return floats;
        return new float[this.floats.length];
    }
    */

    // cloning speed test
    /*
    private boolean[] boolsCloning;
    private byte[] bytesCloning;
    private short[] shortsCloning;
    private int[] intsCloning;
    private float[] floatsCloning;
    */
}

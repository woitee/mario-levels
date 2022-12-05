package mff.forwardmodel.bin.core;

import mff.forwardmodel.bin.level.MarioLevelBin;
import mff.forwardmodel.bin.sprites.FireballBin;
import mff.forwardmodel.bin.sprites.MarioBin;
import mff.forwardmodel.bin.sprites.ShellBin;
import mff.forwardmodel.common.SpriteTypeCommon;
import mff.forwardmodel.common.LevelPart;
import mff.forwardmodel.common.TileFeaturesCommon;

import java.util.ArrayList;

public class MarioWorldBin {
    private static final int RUNNING = 0;
    private static final int WIN = 1;
    private static final int LOSE = 2;
    private static final int TIME_OUT = 3;

    private static final int SPRITE_TYPE_MASK = 0x0000FFFF;

    private static int getPauseTimer(MarioBinData data) {
        return data.ints[MarioBinData.PAUSE_TIMER];
    }

    private static void incPauseTimer(MarioBinData data, int delta) {
        data.ints[MarioBinData.PAUSE_TIMER] += delta;
    }

    private static int getCurrentTimer(MarioBinData data) {
        return data.ints[MarioBinData.CURRENT_TIMER];
    }

    private static void incCurrentTimer(MarioBinData data, int delta) {
        data.ints[MarioBinData.CURRENT_TIMER] += delta;
    }

    private static void setCurrentTimer(MarioBinData data, int value) {
        data.ints[MarioBinData.CURRENT_TIMER] = value;
    }

    private static int getCurrentTick(MarioBinData data) {
        return data.ints[MarioBinData.CURRENT_TICK];
    }

    private static void incCurrentTick(MarioBinData data, int delta) {
        data.ints[MarioBinData.CURRENT_TICK] += delta;
    }

    private static void setCurrentTick(MarioBinData data, int value) {
        data.ints[MarioBinData.CURRENT_TICK] = value;
    }

    private static int getCoins(MarioBinData data) {
        return data.ints[MarioBinData.COINS];
    }

    private static void setCoins(MarioBinData data, int value) {
        data.ints[MarioBinData.COINS] = value;
    }

    private static int getLives(MarioBinData data) {
        return data.ints[MarioBinData.LIVES];
    }

    private static void setLives(MarioBinData data, int value) {
        data.ints[MarioBinData.LIVES] = value;
    }

    private static int getGameStatusCode(MarioBinData data) {
        return data.ints[MarioBinData.GAME_STATUS_CODE];
    }

    private static void setGameStatusCode(MarioBinData data, int value) {
        data.ints[MarioBinData.GAME_STATUS_CODE] = value;
    }

    private static float getCameraX(MarioBinData data) {
        return data.floats[MarioBinData.CAMERA_X];
    }

    private static void setCameraX(MarioBinData data, float value) {
        data.floats[MarioBinData.CAMERA_X] = value;
    }

    private static float getCameraY(MarioBinData data) {
        return data.floats[MarioBinData.CAMERA_Y];
    }

    private static void setCameraY(MarioBinData data, float value) {
        data.floats[MarioBinData.CAMERA_Y] = value;
    }

    public static void addSprite(int spriteCode, MarioUpdateContextBin updateContext, MarioBinData data) {
        updateContext.addedSprites.add(spriteCode);
        //sprite.alive = true; //TODO: should be already set in all sprites
        BinSpriteMethods.methods[spriteCode & SPRITE_TYPE_MASK].Update(data, spriteCode >>> 16, updateContext);
    }

    public static void removeSprite(int spriteCode, MarioUpdateContextBin updateContext, MarioBinData data) {
        updateContext.removedSprites.add(spriteCode);
        BinSpriteMethods.methods[spriteCode & SPRITE_TYPE_MASK].SetAlive(data, spriteCode >>> 16, false);
    }

    public static void win(MarioBinData data) {
        setGameStatusCode(data, WIN);
    }

    public static void lose(MarioBinData data) {
        setGameStatusCode(data, LOSE);
        MarioBin.setAlive(data, false);
    }

    private static void timeout(MarioBinData data) {
        setGameStatusCode(data, TIME_OUT);
        MarioBin.setAlive(data, false);
    }

    public static void update(boolean[] actions, MarioBinData data) {
        if (getGameStatusCode(data) != RUNNING) {
            return;
        }
        if (getPauseTimer(data) > 0) {
            incPauseTimer(data, -1);
            return;
        }

        if (getCurrentTimer(data) > 0) {
            incCurrentTimer(data, -30);
            if (getCurrentTimer(data) <= 0) {
                setCurrentTimer(data, 0);
                timeout(data);
                return;
            }
        }

        MarioUpdateContextBin updateContext = MarioUpdateContextBin.get();

        // workaround the nonexistence of MarioGame here
        int marioGameWidth = 256;
        int marioGameHeight = 256;

        incCurrentTick(data, 1);
        setCameraX(data, MarioBin.getX(data) - marioGameWidth / 2);
        if (getCameraX(data) + marioGameWidth > MarioLevelBin.getWidth(data)) {
            setCameraX(data, MarioLevelBin.getWidth(data) - marioGameWidth);
        }
        if (getCameraX(data) < 0) {
            setCameraX(data, 0);
        }
        setCameraY(data, MarioBin.getY(data) - marioGameHeight / 2);
        if (getCameraY(data) + marioGameHeight > MarioLevelBin.getHeight(data)) {
            setCameraY(data, MarioLevelBin.getHeight(data) - marioGameHeight);
        }
        if (getCameraY(data) < 0) {
            setCameraY(data, 0);
        }

        updateContext.fireballsOnScreen = 0;

        //TODO instead of "1" there should be some SPRITES_START constant
        for (int i = 1; i < data.sprites[MarioBinData.SPRITES_FIRST_FREE_INDEX]; i++) {
            int spriteCode = data.sprites[i];
            int spriteType = spriteCode & SPRITE_TYPE_MASK;
            int entityIndex = spriteCode >>> 16;

            if (BinSpriteMethods.methods[spriteType].GetX(data, entityIndex) < getCameraX(data) - 64 ||
                BinSpriteMethods.methods[spriteType].GetX(data, entityIndex) > getCameraX(data) + marioGameWidth + 64 ||
                BinSpriteMethods.methods[spriteType].GetY(data, entityIndex) > MarioLevelBin.getHeight(data) + 32) {
                if (BinSpriteMethods.methods[spriteType].GetType(data, entityIndex) == SpriteTypeCommon.MARIO) {
                    lose(data);
                }
                removeSprite(data.sprites[i], updateContext, data);
                continue;
            }
            if (BinSpriteMethods.methods[spriteType].GetType(data, entityIndex) == SpriteTypeCommon.FIREBALL) {
                updateContext.fireballsOnScreen +=1;
            }
        }

        for (int x = (int) getCameraX(data) / 16 - 1; x <= (int) (getCameraX(data) + marioGameWidth) / 16 + 1; x++) {
            for (int y = (int) getCameraY(data) / 16 - 1; y <= (int) (getCameraY(data) + marioGameHeight) / 16 + 1; y++) {
                int dir = 0;
                if (x * 16 + 8 > MarioBin.getX(data) + 16)
                    dir = -1;
                if (x * 16 + 8 < MarioBin.getX(data) - 16)
                    dir = 1;

                SpriteTypeCommon spriteType = MarioLevelBin.getSpriteType(x, y, data);
                if (spriteType != SpriteTypeCommon.NONE) {
                    int newSpriteCode;
                    if (spriteType == SpriteTypeCommon.ENEMY_FLOWER) {
                        newSpriteCode = data.addFlowerEnemy(x * 16 + 17, y * 16 + 17, true, 0, y * 16 + 18, -1);
                    }
                    else {
                        newSpriteCode = spawnEnemy(x * 16 + 8, y * 16 + 15, spriteType, dir, data);
                    }
                    addSprite(newSpriteCode, updateContext, data);
                    MarioLevelBin.setBlock(x, y, 0, data); // remove sprite when it is spawned
                }

                if (dir != 0) {
                    if (MarioLevelBin.getBlockValue(x, y, data) == LevelPart.BULLET_BILL_CANNON.getValue()) {
                        if (getCurrentTick(data) % 100 == 0) {
                            addSprite(data.addBulletBill(x * 16 + 8 + dir * 8, y * 16 + 15, true, dir),
                                    updateContext, data);
                        }
                    }
                }
            }
        }

        updateContext.actions = actions;

        // update
        for (int i = 1; i < data.sprites[MarioBinData.SPRITES_FIRST_FREE_INDEX]; i++) {
            int typeCode = data.sprites[i] & SPRITE_TYPE_MASK;
            int entityIndex = data.sprites[i] >>> 16;

            if (!BinSpriteMethods.methods[typeCode].GetAlive(data, entityIndex)) {
                continue;
            }

            BinSpriteMethods.methods[typeCode].Update(data, entityIndex, updateContext);
        }

        // collide check
        for (int i = 1; i < data.sprites[MarioBinData.SPRITES_FIRST_FREE_INDEX]; i++) {
            int typeCode = data.sprites[i] & SPRITE_TYPE_MASK;
            int entityIndex = data.sprites[i] >>> 16;

            if (!BinSpriteMethods.methods[typeCode].GetAlive(data, entityIndex)) {
                continue;
            }

            BinSpriteMethods.methods[typeCode].CollideCheck(data, entityIndex, updateContext);
        }

        // shell collide check
        for (int shellEntityIndex : updateContext.shellsToCheck) {
            for (int i = 1; i < data.sprites[MarioBinData.SPRITES_FIRST_FREE_INDEX]; i++) {

                int shellSpriteCode = (shellEntityIndex << 16) | SpriteTypeCommon.SHELL.getValue();
                int spriteTypeCode = data.sprites[i] & SPRITE_TYPE_MASK;
                int spriteEntityIndex = data.sprites[i] >>> 16;

                if (data.sprites[i] != shellSpriteCode &&
                    ShellBin.getAlive(data, shellEntityIndex) &&
                    BinSpriteMethods.methods[spriteTypeCode].GetAlive(data, spriteEntityIndex)) {
                        if (BinSpriteMethods.methods[spriteTypeCode].ShellCollideCheck(shellEntityIndex,
                            data, spriteEntityIndex, updateContext)) {
                                removeSprite(data.sprites[i], updateContext, data);
                    }
                }
            }
        }
        updateContext.shellsToCheck.clear();

        // fireball collide check
        for (int fireballEntityIndex : updateContext.fireballsToCheck) {
            for (int i = 1; i < data.sprites[MarioBinData.SPRITES_FIRST_FREE_INDEX]; i++) {

                int fireballSpriteCode = (fireballEntityIndex << 16) | SpriteTypeCommon.FIREBALL.getValue();
                int spriteTypeCode = data.sprites[i] & SPRITE_TYPE_MASK;
                int spriteEntityIndex = data.sprites[i] >>> 16;

                if (data.sprites[i] != fireballSpriteCode &&
                    FireballBin.getAlive(data, fireballEntityIndex) &&
                    BinSpriteMethods.methods[spriteTypeCode].GetAlive(data, spriteEntityIndex)) {
                        if (BinSpriteMethods.methods[spriteTypeCode].FireballCollideCheck(fireballEntityIndex,
                            data, spriteEntityIndex, updateContext))
                                removeSprite(fireballSpriteCode, updateContext, data);
                }
            }
        }
        updateContext.fireballsToCheck.clear();

        MarioLevelBin.update((int) MarioBin.getX(data) / 16, data);

        for (int spriteCode : updateContext.addedSprites) {
            data.addSpriteCode(spriteCode);
        }

        for (int spriteCode : updateContext.removedSprites) {
            BinSpriteMethods.methods[spriteCode & SPRITE_TYPE_MASK].Remove(spriteCode >>> 16, data);
        }
        updateContext.addedSprites.clear();
        updateContext.removedSprites.clear();

        updateContext.actions = null;
        updateContext.fireballsOnScreen = 0;
        MarioUpdateContextBin.back(updateContext);
    }

    private static int spawnEnemy(float x, float y, SpriteTypeCommon enemyType, int dir, MarioBinData data) {
        int facing = dir == 0 ? 1 : dir;
        int height = 24;
        if (enemyType != SpriteTypeCommon.RED_KOOPA && enemyType != SpriteTypeCommon.GREEN_KOOPA
                && enemyType != SpriteTypeCommon.RED_KOOPA_WINGED && enemyType != SpriteTypeCommon.GREEN_KOOPA_WINGED)
            height = 12;
        boolean avoidCliffs = enemyType == SpriteTypeCommon.RED_KOOPA || enemyType == SpriteTypeCommon.RED_KOOPA_WINGED;
        boolean winged = enemyType.getValue() % 2 == 1;
        boolean noFireballDeath = enemyType == SpriteTypeCommon.SPIKY || enemyType == SpriteTypeCommon.SPIKY_WINGED;
        return data.addEnemy(x, y, true, enemyType.getValue(), 0, 0, facing,
                height, false, avoidCliffs, winged, noFireballDeath);
    }

    public static void bump(int xTile, int yTile, boolean canBreakBricks, MarioUpdateContextBin updateContext, MarioBinData data) {
        byte blockValue = MarioLevelBin.getBlockValue(xTile, yTile, data);
        ArrayList<TileFeaturesCommon> features = TileFeaturesCommon.getTileFeatures(blockValue);

        if (features.contains(TileFeaturesCommon.BUMPABLE)) {
            bumpInto(xTile, yTile - 1, updateContext, data);
            MarioLevelBin.setBlock(xTile, yTile, 14, data);

            if (features.contains(TileFeaturesCommon.SPECIAL)) {
                if (!MarioBin.getIsLarge(data)) {
                    addSprite(data.addMushroom(xTile * 16 + 9, yTile * 16 + 8, true, 1, 0,
                            false, 0, 0), updateContext, data);
                } else {
                    addSprite(data.addFireFlower(xTile * 16 + 9, yTile * 16 + 8, true, 0),
                            updateContext, data);
                }
            } else if (features.contains(TileFeaturesCommon.LIFE)) {
                addSprite(data.addLifeMushroom(xTile * 16 + 9, yTile * 16 + 8, true, 1, 0,
                        false, 0, 0), updateContext, data);
            } else {
                MarioBin.collectCoin(data, updateContext);
            }
        }

        if (features.contains(TileFeaturesCommon.BREAKABLE)) {
            bumpInto(xTile, yTile - 1, updateContext, data);
            if (canBreakBricks)
                MarioLevelBin.setBlock(xTile, yTile, 0, data);
        }
    }

    private static void bumpInto(int xTile, int yTile, MarioUpdateContextBin updateContext, MarioBinData data) {
        byte blockValue = MarioLevelBin.getBlockValue(xTile, yTile, data);
        if (blockValue == LevelPart.COIN.getValue()) {
            MarioBin.collectCoin(data, updateContext);
            MarioLevelBin.setBlock(xTile, yTile, 0, data);
        }

        // bump check
        for (int i = 1; i < data.sprites[MarioBinData.SPRITES_FIRST_FREE_INDEX] ; i++) {
            int typeCode = data.sprites[i] & SPRITE_TYPE_MASK;
            int entityIndex = data.sprites[i] >>> 16;

            BinSpriteMethods.methods[typeCode].BumpCheck(xTile, yTile, data, entityIndex, updateContext);
        }
    }
}

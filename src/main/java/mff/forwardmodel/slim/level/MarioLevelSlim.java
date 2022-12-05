package mff.forwardmodel.slim.level;

import engine.core.MarioLevel;
import engine.helper.SpriteType;
import mff.forwardmodel.common.LevelPart;
import mff.forwardmodel.common.SpriteTypeCommon;
import mff.forwardmodel.common.StaticLevel;
import mff.forwardmodel.common.TileFeaturesCommon;

import java.util.ArrayList;
import java.util.Arrays;

public class MarioLevelSlim {
    public int width;
    public int tileWidth;
    public int height;
    public int tileHeight;
    public int exitTileX;

    public StaticLevel staticLevel;
    public static int cutoutTileWidth;

    public byte[] levelCutout;
    public boolean[] aliveFlags;
    public int currentCutoutCenter;
    public int cutoutArrayBeginningIndex; // index of the current array beginning
    public int cutoutLeftBorderX;

    private MarioLevelSlim() { }

    public MarioLevelSlim(MarioLevel level, int cutoutTileWidth, int marioTileX) {
        this.width = level.width;
        this.tileWidth = level.tileWidth;
        this.height = level.height;
        this.tileHeight = level.tileHeight;
        this.exitTileX = level.exitTileX;

        MarioLevelSlim.cutoutTileWidth = cutoutTileWidth;
        if (MarioLevelSlim.cutoutTileWidth > tileWidth)
            MarioLevelSlim.cutoutTileWidth = tileWidth;

        int[][] originalLevelTiles = level.getLevelTiles();
        SpriteType[][] originalSpriteTemplates = level.getSpriteTemplates();

        staticLevel = new StaticLevel();
        staticLevel.tiles = new StaticLevel.LevelTile[originalLevelTiles.length][originalLevelTiles[0].length];
        int dynamicTileCounter = 0;
        for (int x = 0; x < originalLevelTiles.length; x++) {
            for (int y = 0; y < originalLevelTiles[x].length; y++) {
                LevelPart levelPart;
                if (originalLevelTiles[x][y] != 0) {
                    if (originalLevelTiles[x][y] == 39 || originalLevelTiles[x][y] == 40)
                        levelPart = LevelPart.EMPTY; // flag is ignored
                    // pipe top left - might have flower
                    else if (originalLevelTiles[x][y] == 18) {
                        if (originalSpriteTemplates[x][y] == SpriteType.ENEMY_FLOWER)
                            levelPart = LevelPart.PIPE_TOP_LEFT_WITH_FLOWER;
                        else
                            levelPart = LevelPart.PIPE_TOP_LEFT_WITHOUT_FLOWER;
                    }
                    else
                        levelPart = LevelPart.getLevelPart(originalLevelTiles[x][y], true);
                }
                else
                    levelPart = LevelPart.getLevelPart(originalSpriteTemplates[x][y].getValue(), false);

                if (LevelPart.isDynamic(levelPart)) {
                    staticLevel.tiles[x][y] = new StaticLevel.LevelTile(dynamicTileCounter, levelPart);
                    dynamicTileCounter++;
                }
                else
                    staticLevel.tiles[x][y] = new StaticLevel.LevelTile(-1, levelPart);
            }
        }

        levelCutout = new byte[cutoutTileWidth * this.tileHeight];
        aliveFlags = new boolean[dynamicTileCounter];
        Arrays.fill(aliveFlags, true);
        currentCutoutCenter = marioTileX;
        cutoutArrayBeginningIndex = 0;
        cutoutLeftBorderX = marioTileX - cutoutTileWidth / 2;

        int copyStart = marioTileX - cutoutTileWidth / 2;
        int copyEnd = cutoutTileWidth % 2 == 0 ? marioTileX + cutoutTileWidth / 2 - 1 : marioTileX + cutoutTileWidth / 2;

        // cutout shifted to not overlap the map if needed
        if (copyStart < 0) {
            copyStart = 0;
            copyEnd = copyStart + cutoutTileWidth - 1;
            currentCutoutCenter = cutoutTileWidth / 2;
            cutoutLeftBorderX = 0;
        }
        else if (copyEnd >= tileWidth) {
            copyStart = (tileWidth - 1) - (cutoutTileWidth - 1);
            copyEnd = tileWidth - 1;
            int centerShift = cutoutTileWidth % 2 == 0 ? cutoutTileWidth / 2 - 1 : cutoutTileWidth / 2;
            currentCutoutCenter = (tileWidth - 1) - centerShift;
            cutoutLeftBorderX = copyStart;

        }

        int column = 0;
        for (int x = copyStart; x <= copyEnd; x++) {
            for (int y = 0; y < this.tileHeight; y++) {
                if (x < 0 || x >= tileWidth)
                    levelCutout[column * tileHeight + y] = LevelPart.EMPTY.getValue();
                else
                    // no need for alive check, still initializing
                    levelCutout[column * tileHeight + y] = staticLevel.tiles[x][y].levelPart.getValue();
            }
            column++;
        }
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarioLevelSlim that = (MarioLevelSlim) o;
        boolean propertiesEqual = width == that.width &&
                tileWidth == that.tileWidth &&
                height == that.height &&
                tileHeight == that.tileHeight &&
                exitTileX == that.exitTileX &&
                currentCutoutCenter == that.currentCutoutCenter &&
                cutoutLeftBorderX == that.cutoutLeftBorderX;
        if (propertiesEqual)
            System.out.println("LEVEL PROPERTIES EQUAL");
        else
            System.out.println("LEVEL PROPERTIES NOT EQUAL");

        boolean cutoutsEqual = compareCutouts(this, that);

        return propertiesEqual && cutoutsEqual;
    }

    private boolean compareCutouts(MarioLevelSlim l1, MarioLevelSlim l2) {
        byte[] a1 = l1.levelCutout;
        byte[] a2 = l2.levelCutout;

        if (a1.length != a2.length) {
            System.out.println("LEVEL CUTOUTS NOT EQUAL");
            return false;
        }

        int i1, i2;
        for (int i = 0; i < a1.length; i++) {
            i1 = (i + l1.cutoutArrayBeginningIndex) % a1.length;
            i2 = (i + l2.cutoutArrayBeginningIndex) % a2.length;
            if (a1[i1] != a2[i2]) {
                System.out.println("LEVEL CUTOUTS NOT EQUAL");
                return false;
            }
        }

        System.out.println("LEVEL CUTOUTS EQUAL");
        return true;
    }

    public MarioLevelSlim clone() {
        MarioLevelSlim clone = new MarioLevelSlim();
        clone.width = this.width;
        clone.tileWidth = this.tileWidth;
        clone.height = this.height;
        clone.tileHeight = this.tileHeight;
        clone.exitTileX = this.exitTileX;
        clone.staticLevel = this.staticLevel;
        clone.currentCutoutCenter = this.currentCutoutCenter;
        clone.cutoutArrayBeginningIndex = this.cutoutArrayBeginningIndex;
        clone.cutoutLeftBorderX = this.cutoutLeftBorderX;

        clone.levelCutout = new byte[this.levelCutout.length];
        System.arraycopy(this.levelCutout, 0, clone.levelCutout, 0, this.levelCutout.length);

        clone.aliveFlags = new boolean[this.aliveFlags.length];
        System.arraycopy(this.aliveFlags, 0, clone.aliveFlags, 0, this.aliveFlags.length);

        return clone;
    }

    public void update(int marioTileX) {
        if (currentCutoutCenter != marioTileX) {
            if (currentCutoutCenter < marioTileX && cutoutLeftBorderX + cutoutTileWidth != tileWidth) { // move right
                int newColumnIndex = cutoutTileWidth % 2 == 0 ? marioTileX + cutoutTileWidth / 2 - 1 : marioTileX + cutoutTileWidth / 2;
                if (newColumnIndex >= tileWidth) // beyond end of level
                    return;
                int y = 0;
                for (int i = cutoutArrayBeginningIndex; i < cutoutArrayBeginningIndex + tileHeight; i++) {
                    if (staticLevel.tiles[newColumnIndex][y].id == -1 || aliveFlags[staticLevel.tiles[newColumnIndex][y].id])
                        levelCutout[i] = staticLevel.tiles[newColumnIndex][y].levelPart.getValue();
                    else
                        levelCutout[i] = LevelPart.getUsedState(staticLevel.tiles[newColumnIndex][y].levelPart).getValue();
                    y++;
                }
                currentCutoutCenter++;
                cutoutLeftBorderX++;
                cutoutArrayBeginningIndex = (cutoutArrayBeginningIndex + tileHeight) % (cutoutTileWidth * this.tileHeight);
            }
            else if (currentCutoutCenter > marioTileX && cutoutLeftBorderX - 1 >= 0) { // move left
                if (cutoutLeftBorderX <= 0) // left cutout border <= beginning of level
                    return;
                int cutoutLastColumnIndex = cutoutArrayBeginningIndex - tileHeight;
                if (cutoutLastColumnIndex < 0)
                    cutoutLastColumnIndex = (cutoutTileWidth * this.tileHeight) - tileHeight;
                int newColumnIndex = marioTileX - cutoutTileWidth / 2;
                int y = 0;
                for (int i = cutoutLastColumnIndex; i < cutoutLastColumnIndex + tileHeight; i++) {
                    if (staticLevel.tiles[newColumnIndex][y].id == -1
                            || aliveFlags[staticLevel.tiles[newColumnIndex][y].id])
                        levelCutout[i] = staticLevel.tiles[newColumnIndex][y].levelPart.getValue();
                    else
                        levelCutout[i] = LevelPart.getUsedState(staticLevel.tiles[newColumnIndex][y].levelPart).getValue();
                    y++;
                }
                currentCutoutCenter--;
                cutoutLeftBorderX--;
                cutoutArrayBeginningIndex -= tileHeight;
                if (cutoutArrayBeginningIndex < 0)
                    cutoutArrayBeginningIndex = (cutoutTileWidth * this.tileHeight) - tileHeight;
            }
        }
    }

    public boolean isBlocking(int xTile, int yTile, float ya) {
        byte blockValue = this.getBlockValue(xTile, yTile);
        ArrayList<TileFeaturesCommon> features = TileFeaturesCommon.getTileFeatures(blockValue);
        boolean blocking = features.contains(TileFeaturesCommon.BLOCK_ALL);
        blocking |= (ya < 0) && features.contains(TileFeaturesCommon.BLOCK_UPPER);
        blocking |= (ya > 0) && features.contains(TileFeaturesCommon.BLOCK_LOWER);

        return blocking;
    }

    public byte getBlockValue(int xTile, int yTile) {
        if (xTile < 0) {
            xTile = 0;
        }
        if (xTile > this.tileWidth - 1) {
            xTile = this.tileWidth - 1;
        }
        if (yTile < 0 || yTile > this.tileHeight - 1) {
            return LevelPart.EMPTY.getValue();
        }

        byte toReturn = levelCutout[calculateCutoutIndex(xTile, yTile)];
        return LevelPart.checkLevelBlock(toReturn);
    }

    // a block that is set is necessarily dynamic
    public void setBlock(int xTile, int yTile, int index) {
        if (xTile < 0 || yTile < 0 || xTile > this.tileWidth - 1 || yTile > this.tileHeight - 1) {
            return;
        }

        int cutoutIndex = calculateCutoutIndex(xTile, yTile);
        if (levelCutout[cutoutIndex] == LevelPart.PIPE_TOP_LEFT_WITH_FLOWER.getValue()) {
            levelCutout[cutoutIndex] = LevelPart.PIPE_TOP_LEFT_WITHOUT_FLOWER.getValue();
        }
        else {
            levelCutout[cutoutIndex] = (byte) index;
        }
        aliveFlags[staticLevel.tiles[xTile][yTile].id] = false;
    }

    public SpriteTypeCommon getSpriteType(int xTile, int yTile) {
        if (xTile < 0 || yTile < 0 || xTile > this.tileWidth - 1 || yTile > this.tileHeight - 1) {
            return SpriteTypeCommon.NONE;
        }
        return LevelPart.getLevelSprite(levelCutout[calculateCutoutIndex(xTile, yTile)]);
    }

    private int calculateCutoutIndex(int x, int y) {
        int cutoutX = x - cutoutLeftBorderX;
        return (cutoutArrayBeginningIndex + cutoutX * tileHeight + y) % (cutoutTileWidth * this.tileHeight);
    }
}

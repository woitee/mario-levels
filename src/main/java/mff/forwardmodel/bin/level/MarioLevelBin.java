package mff.forwardmodel.bin.level;

import mff.forwardmodel.bin.core.MarioBinData;
import mff.forwardmodel.common.SpriteTypeCommon;
import mff.forwardmodel.common.LevelPart;
import mff.forwardmodel.common.TileFeaturesCommon;

import java.util.ArrayList;

public class MarioLevelBin {
    public static int cutoutTileWidth;
// TODO: some methods might not be needed
    public static int getWidth(MarioBinData data) {
        return data.ints[MarioBinData.WIDTH];
    }

    private static void setWidth(MarioBinData data, int value) {
        data.ints[MarioBinData.WIDTH] = value;
    }

    private static int getTileWidth(MarioBinData data) {
        return data.ints[MarioBinData.TILE_WIDTH];
    }

    private static void setTileWidth(MarioBinData data, int value) {
        data.ints[MarioBinData.TILE_WIDTH] = value;
    }

    public static int getHeight(MarioBinData data) {
        return data.ints[MarioBinData.HEIGHT];
    }

    private static void setHeight(MarioBinData data, int value) {
        data.ints[MarioBinData.HEIGHT] = value;
    }

    private static int getTileHeight(MarioBinData data) {
        return data.ints[MarioBinData.TILE_HEIGHT];
    }

    private static void setTileHeight(MarioBinData data, int value) {
        data.ints[MarioBinData.TILE_HEIGHT] = value;
    }

    private static int getExitTileX(MarioBinData data) {
        return data.ints[MarioBinData.EXIT_TILE_X];
    }

    private static void setExitTileX(MarioBinData data, int value) {
        data.ints[MarioBinData.EXIT_TILE_X] = value;
    }

    private static int getCurrentCutoutCenter(MarioBinData data) {
        return data.ints[MarioBinData.CURRENT_CUTOUT_CENTER];
    }

    private static void incCurrentCutoutCenter(MarioBinData data, int delta) {
        data.ints[MarioBinData.CURRENT_CUTOUT_CENTER] += delta;
    }

    private static int getCutoutArrayBeginningIndex(MarioBinData data) {
        return data.ints[MarioBinData.CUTOUT_ARRAY_BEGINNING_INDEX];
    }

    private static void incCutoutArrayBeginningIndex(MarioBinData data, int delta) {
        data.ints[MarioBinData.CUTOUT_ARRAY_BEGINNING_INDEX] += delta;
    }

    private static void setCutoutArrayBeginningIndex(MarioBinData data, int value) {
        data.ints[MarioBinData.CUTOUT_ARRAY_BEGINNING_INDEX] = value;
    }

    private static int getCutoutLeftBorderX(MarioBinData data) {
        return data.ints[MarioBinData.CUTOUT_LEFT_BORDER_X];
    }

    private static void incCutoutLeftBorderX(MarioBinData data, int delta) {
        data.ints[MarioBinData.CUTOUT_LEFT_BORDER_X] += delta;
    }

    private static boolean getAliveFlag(MarioBinData data, int index) {
        return data.bools[index];
    }

    private static void setAliveFlagToFalse(MarioBinData data, int index) {
        data.bools[index] = false;
    }

    private static byte getLevelCutout(MarioBinData data, int index) {
        return data.bytes[index];
    }

    private static void setLevelCutout(MarioBinData data, int index, byte value) {
        data.bytes[index] = value;
    }

    public static void update(int marioTileX, MarioBinData data) {
        if (getCurrentCutoutCenter(data) != marioTileX) {
            if (getCurrentCutoutCenter(data) < marioTileX && getCutoutLeftBorderX(data) + cutoutTileWidth != getTileWidth(data)) { // move right
                int newColumnIndex = cutoutTileWidth % 2 == 0 ? marioTileX + cutoutTileWidth / 2 - 1 : marioTileX + cutoutTileWidth / 2;
                if (newColumnIndex >= getTileWidth(data)) // beyond end of level
                    return;
                int y = 0;
                for (int i = getCutoutArrayBeginningIndex(data); i < getCutoutArrayBeginningIndex(data) + getTileHeight(data); i++) {
                    if (data.staticLevel.tiles[newColumnIndex][y].id == -1 || getAliveFlag(data, data.staticLevel.tiles[newColumnIndex][y].id))
                        setLevelCutout(data, i, data.staticLevel.tiles[newColumnIndex][y].levelPart.getValue());
                    else
                        setLevelCutout(data, i, LevelPart.getUsedState(data.staticLevel.tiles[newColumnIndex][y].levelPart).getValue());
                    y++;
                }
                incCurrentCutoutCenter(data, 1);
                incCutoutLeftBorderX(data, 1);
                setCutoutArrayBeginningIndex(data, (getCutoutArrayBeginningIndex(data) + getTileHeight(data)) % (cutoutTileWidth * getTileHeight(data)));
            }
            else if (getCurrentCutoutCenter(data) > marioTileX && getCutoutLeftBorderX(data) - 1 >= 0) { // move left
                if (getCutoutLeftBorderX(data) <= 0) // left cutout border <= beginning of level
                    return;
                int cutoutLastColumnIndex = getCutoutArrayBeginningIndex(data) - getTileHeight(data);
                if (cutoutLastColumnIndex < 0)
                    cutoutLastColumnIndex = (cutoutTileWidth * getTileHeight(data)) - getTileHeight(data);
                int newColumnIndex = marioTileX - cutoutTileWidth / 2;
                int y = 0;
                for (int i = cutoutLastColumnIndex; i < cutoutLastColumnIndex + getTileHeight(data); i++) {
                    if (data.staticLevel.tiles[newColumnIndex][y].id == -1
                            || getAliveFlag(data, data.staticLevel.tiles[newColumnIndex][y].id))
                        setLevelCutout(data, i, data.staticLevel.tiles[newColumnIndex][y].levelPart.getValue());
                    else
                        setLevelCutout(data, i, LevelPart.getUsedState(data.staticLevel.tiles[newColumnIndex][y].levelPart).getValue());
                    y++;
                }
                incCurrentCutoutCenter(data, -1);
                incCutoutLeftBorderX(data, -1);
                incCutoutArrayBeginningIndex(data, -getTileHeight(data));
                if (getCutoutArrayBeginningIndex(data) < 0)
                    setCutoutArrayBeginningIndex(data, (cutoutTileWidth * getTileHeight(data)) - getTileHeight(data));
            }
        }
    }

    public boolean isBlocking(int xTile, int yTile, float ya, MarioBinData data) {
        byte blockValue = getBlockValue(xTile, yTile, data);
        ArrayList<TileFeaturesCommon> features = TileFeaturesCommon.getTileFeatures(blockValue);
        boolean blocking = features.contains(TileFeaturesCommon.BLOCK_ALL);
        blocking |= (ya < 0) && features.contains(TileFeaturesCommon.BLOCK_UPPER);
        blocking |= (ya > 0) && features.contains(TileFeaturesCommon.BLOCK_LOWER);

        return blocking;
    }

    public static byte getBlockValue(int xTile, int yTile, MarioBinData data) {
        if (xTile < 0) {
            xTile = 0;
        }
        if (xTile > getTileWidth(data) - 1) {
            xTile = getTileWidth(data) - 1;
        }
        if (yTile < 0 || yTile > getTileHeight(data) - 1) {
            return LevelPart.EMPTY.getValue();
        }

        byte toReturn = getLevelCutout(data, calculateCutoutIndex(xTile, yTile, data));
        return LevelPart.checkLevelBlock(toReturn);
    }

    // a block that is set is necessarily dynamic
    public static void setBlock(int xTile, int yTile, int index, MarioBinData data) {
        if (xTile < 0 || yTile < 0 || xTile > getTileWidth(data) - 1 || yTile > getTileHeight(data) - 1) {
            return;
        }

        if (getLevelCutout(data, calculateCutoutIndex(xTile, yTile, data)) == LevelPart.PIPE_TOP_LEFT_WITH_FLOWER.getValue()) {
            setLevelCutout(data, calculateCutoutIndex(xTile, yTile, data), LevelPart.PIPE_TOP_LEFT_WITHOUT_FLOWER.getValue());
            setAliveFlagToFalse(data, data.staticLevel.tiles[xTile][yTile].id);
        }
        else {
            setLevelCutout(data, calculateCutoutIndex(xTile, yTile, data), (byte) index);
            setAliveFlagToFalse(data, data.staticLevel.tiles[xTile][yTile].id);
        }
    }

    public static SpriteTypeCommon getSpriteType(int xTile, int yTile, MarioBinData data) {
        if (xTile < 0 || yTile < 0 || xTile > getTileWidth(data) - 1 || yTile > getTileHeight(data) - 1) {
            return SpriteTypeCommon.NONE;

        }
        return LevelPart.getLevelSprite(getLevelCutout(data, calculateCutoutIndex(xTile, yTile, data)));
    }

    private static int calculateCutoutIndex(int x, int y, MarioBinData data) {
        int cutoutX = x - getCutoutLeftBorderX(data);
        return (getCutoutArrayBeginningIndex(data) + cutoutX * getTileHeight(data) + y) % (cutoutTileWidth * getTileHeight(data));
    }
}

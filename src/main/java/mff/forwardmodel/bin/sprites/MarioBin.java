package mff.forwardmodel.bin.sprites;

import mff.forwardmodel.bin.core.IBinSpriteMethods;
import mff.forwardmodel.bin.core.MarioBinData;
import mff.forwardmodel.bin.core.MarioUpdateContextBin;
import mff.forwardmodel.common.SpriteTypeCommon;

public class MarioBin {

    public static float getX(MarioBinData data) {
        return data.floats[data.spriteStorageInfo[MarioBinData.FLOATS_MARIO_START] + MarioBinData.MARIO_X];
    }

    public static float getY(MarioBinData data) {
        return data.floats[data.spriteStorageInfo[MarioBinData.FLOATS_MARIO_START] + MarioBinData.MARIO_Y];
    }

    public static void setAlive(MarioBinData data, boolean value) {
        data.bools[data.spriteStorageInfo[MarioBinData.BOOLS_MARIO_START] + MarioBinData.MARIO_ALIVE] = value;
    }

    public static boolean getIsLarge(MarioBinData data) {
        return data.bools[data.spriteStorageInfo[MarioBinData.BOOLS_MARIO_START] + MarioBinData.MARIO_IS_LARGE];
    }

    public static void collectCoin(MarioBinData data, MarioUpdateContextBin updateContext) {
        //TODO
    }

    // TODO
    public static final IBinSpriteMethods methods = new IBinSpriteMethods() {
        @Override
        public float GetX(MarioBinData data, int entityIndex) {
            return 0;
        }

        @Override
        public void SetX(MarioBinData data, int entityIndex, float value) {

        }

        @Override
        public float GetY(MarioBinData data, int entityIndex) {
            return 0;
        }

        @Override
        public void SetY(MarioBinData data, int entityIndex, float value) {

        }

        @Override
        public boolean GetAlive(MarioBinData data, int entityIndex) {
            return false;
        }

        @Override
        public void SetAlive(MarioBinData data, int entityIndex, boolean value) {

        }

        @Override
        public SpriteTypeCommon GetType(MarioBinData data, int entityIndex) {
            return null;
        }

        @Override
        public void Update(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {

        }

        @Override
        public void CollideCheck(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {

        }

        @Override
        public void BumpCheck(int xTile, int yTile, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {

        }

        @Override
        public boolean ShellCollideCheck(int shellEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
            return false;
        }

        @Override
        public boolean FireballCollideCheck(int fireballEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
            return false;
        }

        @Override
        public void Remove(int entityIndex, MarioBinData data) {

        }
    };
}

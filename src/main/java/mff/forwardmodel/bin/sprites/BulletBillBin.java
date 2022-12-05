package mff.forwardmodel.bin.sprites;

//TODO: there is a bullet bill spawner - needs attention

import mff.forwardmodel.bin.core.IBinSpriteMethods;
import mff.forwardmodel.bin.core.MarioBinData;
import mff.forwardmodel.bin.core.MarioUpdateContextBin;
import mff.forwardmodel.common.SpriteTypeCommon;

public class BulletBillBin {

    private static float getX(MarioBinData data, int entityIndex) {
        return data.floats[data.spriteStorageInfo[MarioBinData.FLOATS_BULLET_BILL_START]
                + entityIndex * MarioBinData.BULLET_BILL_FLOATS + MarioBinData.BULLET_BILL_X];
    }

    private static void setX(MarioBinData data, int entityIndex, float value) {
        data.floats[data.spriteStorageInfo[MarioBinData.FLOATS_BULLET_BILL_START]
                + entityIndex * MarioBinData.BULLET_BILL_FLOATS + MarioBinData.BULLET_BILL_X]
                = value;
    }

    private static void update(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
        // TODO
    }

    private static void collideCheck(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
        // TODO
    }

	public static final IBinSpriteMethods methods = new IBinSpriteMethods() {

        @Override
        public final float GetX(MarioBinData data, int entityIndex) {
            return getX(data, entityIndex);
        }

        @Override
        public final void SetX(MarioBinData data, int entityIndex, float value) {
            setX(data, entityIndex, value);
        }

        @Override
        public final float GetY(MarioBinData data, int entityIndex) {
            // TODO
            return 0;
        }

        @Override
        public final void SetY(MarioBinData data, int entityIndex, float value) {
            // TODO
        }

        @Override
        public final boolean GetAlive(MarioBinData data, int entityIndex) {
            // TODO
            return false;
        }

        @Override
        public final void SetAlive(MarioBinData data, int entityIndex, boolean value) {
            // TODO
        }

        @Override
        public final SpriteTypeCommon GetType(MarioBinData data, int entityIndex) {
            // TODO
            return null;
        }

        @Override
        public final void Update(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
            update(data, entityIndex, updateContext);
        }

        @Override
        public final void CollideCheck(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
            collideCheck(data, entityIndex, updateContext);
        }

        @Override
        public final void BumpCheck(int xTile, int yTile, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
            // TODO
        }

        @Override
        public final boolean ShellCollideCheck(int shellEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
            // TODO
            return false;
        }

        @Override
        public final boolean FireballCollideCheck(int fireballEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
            // TODO
            return false;
        }

        @Override
        public final void Remove(int entityIndex, MarioBinData data) {
            data.removeBulletBill(entityIndex);
        }

    };

    /*
	public static int getFacing(MarioBinData data, int entityIndex) {
    	return data[entityIndex * 4];
    }
    
    public static int setFacing(MarioBinData data, int entityIndex, int value) {
    	data.floats[entityIndex * 4] = value;
    }
    
    public static int incFacing(MarioBinData data, int entityIndex, int delta) {
    	data.floats[entityIndex * 4] += delta;
    }
    */
	
}

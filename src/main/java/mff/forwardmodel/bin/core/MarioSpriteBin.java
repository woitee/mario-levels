package mff.forwardmodel.bin.core;
// TODO: probably not needed, solved in IBinSpriteMethods

/*
 * A method can't be abstract if it's static, and we need getX, setX, etc. methods
 * to be static, so we have no way of enforcing implementation. An exception is thrown
 * to inform that a method hasn't been overridden in a sprite.
 */
/*
public abstract class MarioSpriteBin {

    public static float getX(MarioBinData data, int entityIndex) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static void setX(MarioBinData data, int entityIndex, float value) {
    	throw new UnsupportedOperationException("Not implemented!");
    }

    public static float getY(MarioBinData data, int entityIndex) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static void setY(MarioBinData data, int entityIndex, float value) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static boolean getAlive(MarioBinData data, int entityIndex) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static void setAlive(MarioBinData data, int entityIndex, boolean value) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static SpriteTypeCommon getType(MarioBinData data, int entityIndex) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public static void update(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    // with the following methods that aren't implemented in all sprites we can't force implementation at all
    public static void collideCheck(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {

    }
    public static void bumpCheck(int xTile, int yTile, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {

    }
    public static boolean shellCollideCheck(int shellEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
        return false;
    }
    public static boolean fireballCollideCheck(int fireballEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext) {
        return false;
    }
}
*/
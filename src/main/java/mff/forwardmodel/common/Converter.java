package mff.forwardmodel.common;

import engine.core.MarioForwardModel;
import mff.forwardmodel.bin.core.MarioBinData;
import mff.forwardmodel.bin.core.MarioForwardModelBin;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;
import mff.forwardmodel.slim.core.MarioWorldSlim;

public class Converter {
    public static MarioForwardModelSlim originalToSlim(MarioForwardModel originalModel, int levelCutoutTileWidth) {
        MarioWorldSlim marioWorldSlim = new MarioWorldSlim(originalModel.getWorld(), levelCutoutTileWidth);
        return new MarioForwardModelSlim(marioWorldSlim);
    }

    public static MarioForwardModelBin slimToBin(MarioForwardModelSlim slimModel) {
        MarioBinData marioBinData = new MarioBinData(slimModel.getWorld());
        return new MarioForwardModelBin(marioBinData);
    }
}

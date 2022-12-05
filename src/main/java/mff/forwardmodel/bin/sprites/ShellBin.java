package mff.forwardmodel.bin.sprites;

import mff.forwardmodel.bin.core.MarioBinData;

public class ShellBin {

    public static boolean getAlive(MarioBinData data, int entityIndex) {
        return data.bools[data.spriteStorageInfo[MarioBinData.BOOLS_SHELL_START]
                + entityIndex * MarioBinData.SHELL_BOOLS + MarioBinData.SHELL_ALIVE];
    }

}

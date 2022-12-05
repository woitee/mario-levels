package mff.forwardmodel.bin.core;

public class MarioForwardModelBin {

    private final MarioBinData data;

    public MarioForwardModelBin(MarioBinData data) {
        this.data = data;
    }

    public void advance(boolean[] actions) {
        MarioWorldBin.update(actions, data);
    }

    public MarioForwardModelBin clone() {
        return new MarioForwardModelBin(this.data.clone());
    }

    // pooling test
    /*
    public void returnArrays() {
        data.returnArrays();
    }
    */
}

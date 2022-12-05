package mff.forwardmodel.slim.core;

import mff.forwardmodel.slim.sprites.FireballSlim;
import mff.forwardmodel.slim.sprites.ShellSlim;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class MarioUpdateContextSlim {

    public MarioWorldSlim world;
    public boolean[] actions;
    public int fireballsOnScreen;

    public final ArrayList<FireballSlim> fireballsToCheck = new ArrayList<>();
    public final ArrayList<ShellSlim> shellsToCheck = new ArrayList<>();
    final ArrayList<MarioSpriteSlim> addedSprites = new ArrayList<>();
    final ArrayList<MarioSpriteSlim> removedSprites = new ArrayList<>();

    private static final ArrayDeque<MarioUpdateContextSlim> pool = new ArrayDeque<>();
    //private static final ArrayBlockingQueue<MarioUpdateContextSlim> pool = new ArrayBlockingQueue<>(); //TODO might be thread safe and fast?

    public static MarioUpdateContextSlim get() {
        MarioUpdateContextSlim ctx = pool.poll();
        if (ctx != null) return ctx;

        MarioUpdateContextSlim newCtx = new MarioUpdateContextSlim();
        newCtx.addedSprites.ensureCapacity(10);
        newCtx.removedSprites.ensureCapacity(10);
        return newCtx;
    }

    static void back(MarioUpdateContextSlim ctx) {
        pool.add(ctx);
    }
}

package mff.forwardmodel.bin.core;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MarioUpdateContextBin {

    public boolean[] actions;
    public int fireballsOnScreen;

    // fireball entity indexes
    public final ArrayList<Integer> fireballsToCheck = new ArrayList<>();

    // shell entity indexes
    public final ArrayList<Integer> shellsToCheck = new ArrayList<>();

    // sprites codes = type + id
    final ArrayList<Integer> addedSprites = new ArrayList<>();
    final ArrayList<Integer> removedSprites = new ArrayList<>();

    private static final ConcurrentLinkedQueue<MarioUpdateContextBin> pool = new ConcurrentLinkedQueue<>();

    public static MarioUpdateContextBin get() {
        MarioUpdateContextBin ctx = pool.poll();
        if (ctx != null) return ctx;
        return new MarioUpdateContextBin();
    }

    static void back(MarioUpdateContextBin ctx) {
        pool.add(ctx);
    }
}

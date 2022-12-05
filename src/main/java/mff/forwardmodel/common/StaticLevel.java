package mff.forwardmodel.common;

public class StaticLevel {
    public LevelTile[][] tiles;

    public static class LevelTile {
        public final int id;
        public final LevelPart levelPart;

        public LevelTile(int id, LevelPart levelPart) {
            this.id = id;
            this.levelPart = levelPart;
        }
    }
}

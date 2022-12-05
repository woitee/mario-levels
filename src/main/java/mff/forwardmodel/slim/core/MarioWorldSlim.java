package mff.forwardmodel.slim.core;

import engine.core.MarioSprite;
import engine.core.MarioWorld;
import engine.helper.GameStatus;
import mff.forwardmodel.common.SpriteTypeCommon;
import engine.sprites.*;
import mff.forwardmodel.common.LevelPart;
import mff.forwardmodel.slim.level.MarioLevelSlim;
import mff.forwardmodel.common.TileFeaturesCommon;
import mff.forwardmodel.slim.sprites.*;

import java.util.ArrayList;

public class MarioWorldSlim {
    public static final int RUNNING = 0;
    public static final int WIN = 1;
    public static final int LOSE = 2;
    public static final int TIME_OUT = 3;

    // workaround the nonexistence of MarioGame here
    public static int marioGameWidth = 256;
    public static int marioGameHeight = 256;

    public int gameStatusCode;
    public int pauseTimer;
    public int currentTimer;
    public float cameraX;
    public float cameraY;
    public MarioSlim mario;
    public MarioLevelSlim level;
    public int currentTick;
    public int coins, lives;

    public ArrayList<MarioSpriteSlim> sprites;

    private MarioWorldSlim() { }

    public MarioWorldSlim(MarioWorld originalWorld, int levelCutoutTileWidth) {
        this.gameStatusCode = convertGameStatus(originalWorld.gameStatus);
        this.pauseTimer = originalWorld.pauseTimer;
        this.currentTimer = originalWorld.currentTimer;
        this.cameraX = originalWorld.cameraX;
        this.cameraY = originalWorld.cameraY;
        this.currentTick = originalWorld.currentTick;
        this.coins = originalWorld.coins;
        this.lives = originalWorld.lives;

        sprites = new ArrayList<>(16);

        for (MarioSprite originalSprite : originalWorld.getSpriteClones()) {
            if (originalSprite instanceof BulletBill)
                this.sprites.add(new BulletBillSlim((BulletBill) originalSprite));
            else if (originalSprite instanceof FlowerEnemy)
                this.sprites.add(new FlowerEnemySlim((FlowerEnemy) originalSprite));
            else if (originalSprite instanceof Enemy)
                this.sprites.add(new EnemySlim((Enemy) originalSprite));
            else if (originalSprite instanceof Fireball)
                this.sprites.add(new FireballSlim((Fireball) originalSprite));
            else if (originalSprite instanceof FireFlower)
                this.sprites.add(new FireFlowerSlim((FireFlower) originalSprite));
            else if (originalSprite instanceof LifeMushroom)
                this.sprites.add(new LifeMushroomSlim((LifeMushroom) originalSprite));
            else if (originalSprite instanceof Mario) {
                mario = new MarioSlim((Mario) originalSprite);
                this.sprites.add(mario);
            }
            else if (originalSprite instanceof Mushroom)
                this.sprites.add(new MushroomSlim((Mushroom) originalSprite));
            else if (originalSprite instanceof Shell)
                this.sprites.add(new ShellSlim((Shell) originalSprite));
            else
                throw new IllegalArgumentException();
        }

        // minimum width because world.update method might look this far (e.g. isBlocking in enemy move)
        if (levelCutoutTileWidth < 27) {
            levelCutoutTileWidth = 27;
            System.out.println("Cutout width increased to 27 to prevent errors.");
        }

        if (this.mario == null) {
            this.mario = new MarioSlim(originalWorld.mario);
            this.sprites.add(mario);
        }

        this.level = new MarioLevelSlim(originalWorld.level, levelCutoutTileWidth, (int) mario.x / 16);
    }

    private int convertGameStatus(GameStatus gameStatus) {
        switch (gameStatus) {
            case RUNNING:
                return RUNNING;
            case WIN:
                return WIN;
            case LOSE:
                return LOSE;
            case TIME_OUT:
                return TIME_OUT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarioWorldSlim that = (MarioWorldSlim) o;
        boolean worldPropertiesEqual = pauseTimer == that.pauseTimer &&
                currentTimer == that.currentTimer &&
                Float.compare(that.cameraX, cameraX) == 0 &&
                Float.compare(that.cameraY, cameraY) == 0 &&
                currentTick == that.currentTick &&
                coins == that.coins &&
                lives == that.lives &&
                gameStatusCode == that.gameStatusCode;
        if (worldPropertiesEqual)
            System.out.println("WORLD PROPERTIES EQUAL");
        else
            System.out.println("WORLD PROPERTIES NOT EQUAL");

        // align level cutouts if game is not running TODO: is actually needed?
        if (this.gameStatusCode != RUNNING)
            this.level.update((int) this.mario.x / 16);
        if (that.gameStatusCode != RUNNING)
            that.level.update((int) that.mario.x / 16);

        return worldPropertiesEqual & level.deepEquals(that.level) &
                areSpritesEqual(this.sprites, that.sprites);
    }

    private boolean areSpritesEqual(ArrayList<MarioSpriteSlim> sprites1, ArrayList<MarioSpriteSlim> sprites2) {
        for (MarioSpriteSlim sprite1 : sprites1) {
            boolean found = false;
            System.out.println("  Comparing sprite " + sprite1 + ", sprite type: " + sprite1.getType());
            for (MarioSpriteSlim sprite2 : sprites2) {
                if (sprite1.deepEquals(sprite2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("SPRITES NOT EQUAL");
                System.out.println("    Sprite not equal: " + sprite1 + ", sprite type: "
                        + sprite1.getType() + ", sprite X: " + sprite1.x);
                return false;
            }
        }
        System.out.println("SPRITES EQUAL");
        return true;
    }

    public MarioWorldSlim clone() {
        MarioWorldSlim clone = new MarioWorldSlim();
        clone.gameStatusCode = this.gameStatusCode;
        clone.pauseTimer = this.pauseTimer;
        clone.currentTimer = this.currentTimer;
        clone.cameraX = this.cameraX;
        clone.cameraY = this.cameraY;
        clone.currentTick = this.currentTick;
        clone.coins = this.coins;
        clone.lives = this.lives;

        clone.level = this.level.clone();

        clone.sprites = new ArrayList<>();

        for (MarioSpriteSlim sprite : this.sprites) {
            MarioSpriteSlim spriteClone = sprite.clone();
            clone.sprites.add(spriteClone);
            if (spriteClone.getType() == SpriteTypeCommon.MARIO)
                clone.mario = (MarioSlim) spriteClone;
        }

        if (clone.mario == null)
            clone.mario = (MarioSlim) this.mario.clone();

        return clone;
    }

    public ArrayList<MarioSpriteSlim> getEnemies() {
        ArrayList<MarioSpriteSlim> enemies = new ArrayList<>();
        for (MarioSpriteSlim sprite : sprites) {
            if (this.isEnemy(sprite)) {
                enemies.add(sprite);
            }
        }
        return enemies;
    }

    public void addSprite(MarioSpriteSlim sprite, MarioUpdateContextSlim updateContext) {
        updateContext.addedSprites.add(sprite);
        sprite.alive = true;
        sprite.update(updateContext);
    }

    public void removeSprite(MarioSpriteSlim sprite, MarioUpdateContextSlim updateContext) {
        updateContext.removedSprites.add(sprite);
        sprite.alive = false;
    }

    public void win() {
        this.gameStatusCode = WIN;
    }

    public void lose() {
        this.gameStatusCode = LOSE;
        this.mario.alive = false;
    }

    private void timeout() {
        this.gameStatusCode = TIME_OUT;
        this.mario.alive = false;
    }

    private boolean isEnemy(MarioSpriteSlim sprite) {
        return sprite instanceof EnemySlim || sprite instanceof FlowerEnemySlim || sprite instanceof BulletBillSlim;
    }

    public void update(boolean[] actions) {
        if (this.gameStatusCode != RUNNING) {
            return;
        }
        if (this.pauseTimer > 0) {
            this.pauseTimer -= 1;
            return;
        }

        if (this.currentTimer > 0) {
            this.currentTimer -= 30;
            if (this.currentTimer <= 0) {
                this.currentTimer = 0;
                this.timeout();
                return;
            }
        }

        MarioUpdateContextSlim updateContext = MarioUpdateContextSlim.get();
        updateContext.world = this;

        this.currentTick += 1;
        this.cameraX = this.mario.x - marioGameWidth / 2;
        if (this.cameraX + marioGameWidth > this.level.width) {
            this.cameraX = this.level.width - marioGameWidth;
        }
        if (this.cameraX < 0) {
            this.cameraX = 0;
        }
        this.cameraY = this.mario.y - marioGameHeight / 2;
        if (this.cameraY + marioGameHeight > this.level.height) {
            this.cameraY = this.level.height - marioGameHeight;
        }
        if (this.cameraY < 0) {
            this.cameraY = 0;
        }

        updateContext.fireballsOnScreen = 0;
        for (MarioSpriteSlim sprite : sprites) {
            if (sprite.x < cameraX - 64 || sprite.x > cameraX + marioGameWidth + 64 || sprite.y > this.level.height + 32) {
                if (sprite.getType() == SpriteTypeCommon.MARIO) {
                    this.lose();
                }
                this.removeSprite(sprite, updateContext);
                continue;
            }
            if (sprite.getType() == SpriteTypeCommon.FIREBALL) {
                updateContext.fireballsOnScreen += 1;
            }
        }

        // sprites spawning, sufficient to check right column
        int rightColumnX = (int) (cameraX + marioGameWidth) / 16 + 1;

        int dir = 0;
        if (rightColumnX * 16 + 8 > mario.x + 16)
            dir = -1;
        if (rightColumnX * 16 + 8 < mario.x - 16)
            dir = 1;

        for (int y = (int) cameraY / 16 - 1; y <= (int) (cameraY + marioGameHeight) / 16 + 1; y++) {
            SpriteTypeCommon spriteType = level.getSpriteType(rightColumnX, y);
            if (spriteType != SpriteTypeCommon.NONE) {
                MarioSpriteSlim newSprite = this.spawnEnemy(spriteType, rightColumnX, y, dir);
                this.addSprite(newSprite, updateContext);
                level.setBlock(rightColumnX, y, 0); // remove sprite when it is spawned
            }
        }

        // Bullet Bills spawning, check for cannons
        for (int x = (int) cameraX / 16 - 1; x <= (int) (cameraX + marioGameWidth) / 16 + 1; x++) {
            dir = 0;
            if (x * 16 + 8 > mario.x + 16)
                dir = -1;
            if (x * 16 + 8 < mario.x - 16)
                dir = 1;

            for (int y = (int) cameraY / 16 - 1; y <= (int) (cameraY + marioGameHeight) / 16 + 1; y++) {
                if (dir != 0) {
                    if (this.level.getBlockValue(x, y) == LevelPart.BULLET_BILL_CANNON.getValue()) {
                        if (this.currentTick % 100 == 0) {
                            addSprite(new BulletBillSlim(x * 16 + 8 + dir * 8, y * 16 + 15, dir), updateContext);
                        }
                    }
                }
            }
        }

        updateContext.actions = actions;

        for (MarioSpriteSlim sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.update(updateContext);
        }
        for (MarioSpriteSlim sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.collideCheck(updateContext);
        }

        for (ShellSlim shell : updateContext.shellsToCheck) {
            for (MarioSpriteSlim sprite : sprites) {
                if (sprite != shell && shell.alive && sprite.alive) {
                    if (sprite.shellCollideCheck(shell, updateContext)) {
                        this.removeSprite(sprite, updateContext);
                    }
                }
            }
        }
        if (updateContext.shellsToCheck.size() != 0)
            updateContext.shellsToCheck.clear();

        for (FireballSlim fireball : updateContext.fireballsToCheck) {
            for (MarioSpriteSlim sprite : sprites) {
                if (sprite != fireball && fireball.alive && sprite.alive) {
                    if (sprite.fireballCollideCheck(fireball, updateContext)) {
                        this.removeSprite(fireball, updateContext);
                    }
                }
            }
        }
        if (updateContext.fireballsToCheck.size() != 0)
            updateContext.fireballsToCheck.clear();

        this.level.update((int) mario.x / 16);

        for (MarioSpriteSlim newSprite : updateContext.addedSprites) {
            sprites.add(newSprite);
        }
        for (MarioSpriteSlim removedSprite : updateContext.removedSprites) {
            sprites.remove(removedSprite);
        }

        if (updateContext.addedSprites.size() != 0)
            updateContext.addedSprites.clear();
        if (updateContext.removedSprites.size() != 0)
            updateContext.removedSprites.clear();

        updateContext.world = null;
        updateContext.actions = null;
        updateContext.fireballsOnScreen = 0;
        MarioUpdateContextSlim.back(updateContext);
    }

    /**
     * @param rightWindowBorderX in pixels (position * 16)
     */
    public void updateWindow(boolean[] actions, int rightWindowBorderX) {
        // symmetrical cutout, left from window does not matter that much
        int leftWindowBorderX = (int) (rightWindowBorderX - (2 * (rightWindowBorderX - mario.x)));

        if (this.gameStatusCode != RUNNING) {
            return;
        }
        if (this.pauseTimer > 0) {
            this.pauseTimer -= 1;
            return;
        }

        if (this.currentTimer > 0) {
            this.currentTimer -= 30;
            if (this.currentTimer <= 0) {
                this.currentTimer = 0;
                this.timeout();
                return;
            }
        }

        MarioUpdateContextSlim updateContext = MarioUpdateContextSlim.get();
        updateContext.world = this;

        this.currentTick += 1;
        this.cameraX = this.mario.x - marioGameWidth / 2;
        if (this.cameraX + marioGameWidth > this.level.width) {
            this.cameraX = this.level.width - marioGameWidth;
        }
        if (this.cameraX < 0) {
            this.cameraX = 0;
        }
        this.cameraY = this.mario.y - marioGameHeight / 2;
        if (this.cameraY + marioGameHeight > this.level.height) {
            this.cameraY = this.level.height - marioGameHeight;
        }
        if (this.cameraY < 0) {
            this.cameraY = 0;
        }

        float leftKillBorder = Math.max(leftWindowBorderX, cameraX - 64);
        float rightKillBorder = Math.min(rightWindowBorderX, cameraX + marioGameWidth + 64);

        updateContext.fireballsOnScreen = 0;
        for (MarioSpriteSlim sprite : sprites) {
            // kill all sprites outside of selected window
            if (sprite.x < leftKillBorder || sprite.x > rightKillBorder || sprite.y > this.level.height + 32) {
                if (sprite.getType() == SpriteTypeCommon.MARIO) {
                    this.lose();
                }
                this.removeSprite(sprite, updateContext);
                continue;
            }
            if (sprite.getType() == SpriteTypeCommon.FIREBALL) {
                updateContext.fireballsOnScreen += 1;
            }
        }

        // no new sprites outside selected window
        int fromX = Math.max((int) cameraX / 16 - 1, leftWindowBorderX / 16);
        int toX = Math.min((int) (cameraX + marioGameWidth) / 16 + 1, rightWindowBorderX / 16);

        int dir = 0;
        if (toX * 16 + 8 > mario.x + 16)
            dir = -1;
        if (toX * 16 + 8 < mario.x - 16)
            dir = 1;

        for (int y = (int) cameraY / 16 - 1; y <= (int) (cameraY + marioGameHeight) / 16 + 1; y++) {
            SpriteTypeCommon spriteType = level.getSpriteType(toX, y);
            if (spriteType != SpriteTypeCommon.NONE) {
                MarioSpriteSlim newSprite = this.spawnEnemy(spriteType, toX, y, dir);
                this.addSprite(newSprite, updateContext);
                level.setBlock(toX, y, 0); // remove sprite when it is spawned
            }
        }

        for (int x = fromX; x <= toX; x++) {
            dir = 0;
            if (x * 16 + 8 > mario.x + 16)
                dir = -1;
            if (x * 16 + 8 < mario.x - 16)
                dir = 1;

            for (int y = (int) cameraY / 16 - 1; y <= (int) (cameraY + marioGameHeight) / 16 + 1; y++) {
                if (dir != 0) {
                    if (this.level.getBlockValue(x, y) == LevelPart.BULLET_BILL_CANNON.getValue()) {
                        if (this.currentTick % 100 == 0) {
                            addSprite(new BulletBillSlim(x * 16 + 8 + dir * 8, y * 16 + 15, dir), updateContext);
                        }
                    }
                }
            }
        }

        updateContext.actions = actions;

        for (MarioSpriteSlim sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.update(updateContext);
        }
        for (MarioSpriteSlim sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.collideCheck(updateContext);
        }

        for (ShellSlim shell : updateContext.shellsToCheck) {
            for (MarioSpriteSlim sprite : sprites) {
                if (sprite != shell && shell.alive && sprite.alive) {
                    if (sprite.shellCollideCheck(shell, updateContext)) {
                        this.removeSprite(sprite, updateContext);
                    }
                }
            }
        }
        if (updateContext.shellsToCheck.size() != 0)
            updateContext.shellsToCheck.clear();

        for (FireballSlim fireball : updateContext.fireballsToCheck) {
            for (MarioSpriteSlim sprite : sprites) {
                if (sprite != fireball && fireball.alive && sprite.alive) {
                    if (sprite.fireballCollideCheck(fireball, updateContext)) {
                        this.removeSprite(fireball, updateContext);
                    }
                }
            }
        }
        if (updateContext.fireballsToCheck.size() != 0)
            updateContext.fireballsToCheck.clear();

        this.level.update((int) mario.x / 16);

        for (MarioSpriteSlim newSprite : updateContext.addedSprites) {
            sprites.add(newSprite);
        }
        for (MarioSpriteSlim removedSprite : updateContext.removedSprites) {
            sprites.remove(removedSprite);
        }

        if (updateContext.addedSprites.size() != 0)
            updateContext.addedSprites.clear();
        if (updateContext.removedSprites.size() != 0)
            updateContext.removedSprites.clear();

        updateContext.world = null;
        updateContext.actions = null;
        updateContext.fireballsOnScreen = 0;
        MarioUpdateContextSlim.back(updateContext);
    }

    private MarioSpriteSlim spawnEnemy(SpriteTypeCommon type, int x, int y, int dir) {
        if (type == SpriteTypeCommon.ENEMY_FLOWER) {
            // flower enemy constructor needs to call update - which uses world
            MarioUpdateContextSlim updateContext = MarioUpdateContextSlim.get();
            updateContext.world = this;

            FlowerEnemySlim flowerEnemy = new FlowerEnemySlim(x * 16 + 17, y * 16 + 18, updateContext);

            updateContext.world = null;
            MarioUpdateContextSlim.back(updateContext);

            return flowerEnemy;
        }
        else
            return new EnemySlim(x * 16 + 8, y * 16 + 15, dir, type);
    }

    public void bump(int xTile, int yTile, boolean canBreakBricks, MarioUpdateContextSlim updateContext) {
        byte blockValue = this.level.getBlockValue(xTile, yTile);
        ArrayList<TileFeaturesCommon> features = TileFeaturesCommon.getTileFeatures(blockValue);

        if (features.contains(TileFeaturesCommon.BUMPABLE)) {
            bumpInto(xTile, yTile - 1, updateContext);
            level.setBlock(xTile, yTile, 14);

            if (features.contains(TileFeaturesCommon.SPECIAL)) {
                if (!this.mario.isLarge) {
                    addSprite(new MushroomSlim(xTile * 16 + 9, yTile * 16 + 8), updateContext);
                } else {
                    addSprite(new FireFlowerSlim(xTile * 16 + 9, yTile * 16 + 8), updateContext);
                }
            } else if (features.contains(TileFeaturesCommon.LIFE)) {
                addSprite(new LifeMushroomSlim(xTile * 16 + 9, yTile * 16 + 8), updateContext);
            } else {
                mario.collectCoin(updateContext);
            }
        }

        if (features.contains(TileFeaturesCommon.BREAKABLE)) {
            bumpInto(xTile, yTile - 1, updateContext);
            if (canBreakBricks)
                level.setBlock(xTile, yTile, 0);
        }
    }

    private void bumpInto(int xTile, int yTile, MarioUpdateContextSlim updateContext) {
        byte blockValue = level.getBlockValue(xTile, yTile);
        if (blockValue == LevelPart.COIN.getValue()) {
            this.mario.collectCoin(updateContext);
            level.setBlock(xTile, yTile, 0);
        }

        for (MarioSpriteSlim sprite : sprites) {
            sprite.bumpCheck(xTile, yTile, updateContext);
        }
    }
}

package io.github.jwharm.puzzlegame.engine;

public class GameState {

    private int level = 1, lives = 3, keys = 0, gems = 0, score = 0;
    private boolean won = false;

    public int level() {
        return level;
    }

    public void nextLevel() {
        level++;
        won = false;
    }

    public void win() {
        won = true;
    }

    public int lives() {
        return lives;
    }

    public void die() {
        lives--;
    }

    public int keys() {
        return keys;
    }

    public void keyCollected() {
        keys++;
    }

    public int gems() {
        return gems;
    }

    public void gemCollected() {
        gems++;
    }

    public int score() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
    }
}

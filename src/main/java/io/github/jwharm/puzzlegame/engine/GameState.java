package io.github.jwharm.puzzlegame.engine;

public class GameState {

    private int level = 1, lives = 3, score = 0, bonus = 300;
    private boolean won = false;

    public int level() {
        return level;
    }

    public void goToNextLevel() {
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

    public void keyCollected() {
        score += 10;
    }

    public void gemCollected() {
        score += 50;
    }

    public void decreaseBonus() {
        bonus--;
    }

    public int score() {
        return score;
    }

    public int bonus() {
        return bonus;
    }
}

package io.github.jwharm.puzzlegame.engine;

public class GameState {

    private static final int START_BONUS = 300;
    private int room, lives, score, startScore, bonus;
    private String message;

    public GameState(int room, int lives, int score) {
        this.room = room;
        this.lives = lives;
        this.score = score;
        this.startScore = 0;
        this.bonus = START_BONUS;
        this.message = null;
    }

    public void reset() {
        score = startScore;
    }

    public void die() {
        if (lives > 0)
            lives--;
        bonus = START_BONUS;
    }

    public void roomCompleted() {
        score += bonus;
        startScore = score;
        room++;
        bonus = START_BONUS;
    }

    public void keyCollected() {
        score += 10;
    }

    public void gemCollected() {
        score += 50;
    }

    public void decreaseBonus() {
        if (bonus > 0)
            bonus--;
    }

    public void showMessage(String message) {
        this.message = message;
    }
    
    public void hideMessage() {
        this.message = null;
    }

    public int room() {
        return room;
    }

    public int lives() {
        return lives;
    }

    public int score() {
        return score;
    }

    public int bonus() {
        return bonus;
    }

    public String message() {
        return message;
    }
}

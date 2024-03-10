package io.github.jwharm.puzzlegame.engine;

public class GameState {

    private int room = 1, lives = 3, score = 0, startScore = 0, bonus = 300;

    public int room() {
        return room;
    }

    public void goToNextRoom() {
        score += bonus;
        startScore = score;
        room++;
        bonus = 300;
    }

    public int lives() {
        return lives;
    }

    public void die() {
        lives--;
        score = startScore;
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

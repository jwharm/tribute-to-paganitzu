/* Tribute to Paganitzu, a simple puzzle-game engine
 * Copyright (C) 2024 Jan-Willem Harmannij
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.jwharm.tribute.engine;

import java.io.Serializable;

/**
 * This class maintains global state of the game: the current room, the number
 * of lives, the score, the starting score of the room, the remaining bonus
 * points, and whether the room is currently dark (specifically room 17).
 * <p>
 * The "message" field contains a text message that must be displayed on-screen.
 * It will be cleared after the message dialog has been displayed.
 */
public class GameState implements Serializable {

    private static final int START_BONUS = 300;
    private static final int KEY_BONUS = 10;
    private static final int GEM_BONUS = 50;
    private static final int REWARD_BONUS = 200;
    private static final int REWARD_LIFE = 5000;
    private static final int NUM_ROOMS = 20;
    private int room, lives, score, startScore, bonus, nextLifeThreshold;
    private boolean dark;
    private String message;

    public GameState(int room, int lives, int score) {
        this.room = room;
        this.lives = lives;
        this.score = score;
        this.startScore = 0;
        this.bonus = START_BONUS;
        this.dark = (room == 17);
        this.message = null;

        // Set the score threshold to add an extra life
        nextLifeThreshold = REWARD_LIFE;
        while (nextLifeThreshold < score)
            nextLifeThreshold += REWARD_LIFE;
    }

    public void reset() {
        score = startScore;
        bonus = START_BONUS;
        this.dark = (room == 17);
    }

    public void die() {
        lives--;

        if (lives < 0) {
            message = "Game over.";
            room = 1;
            score = 0;
            startScore = 0;
            bonus = START_BONUS;
        }
    }

    private void addScore(int increase) {
        score += increase;
        while (score >= nextLifeThreshold) {
            lives++;
            nextLifeThreshold += REWARD_LIFE;
        }
    }

    public void roomCompleted() {
        addScore(bonus);
        startScore = score;
        room = (room + 1) % NUM_ROOMS;
    }

    public void keyCollected() {
        addScore(KEY_BONUS);
    }

    public void gemCollected() {
        addScore(GEM_BONUS);
    }

    public void decreaseBonus() {
        if (bonus > 0)
            bonus--;
    }

    public void addBonusReward() {
        addScore(REWARD_BONUS);
    }

    public void showMessage(String message) {
        this.message = message;
    }
    
    public void clearMessage() {
        this.message = null;
    }

    public void makeLight() {
        this.dark = false;
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

    public boolean dark() {
        return dark;
    }

    public String message() {
        return message;
    }
}

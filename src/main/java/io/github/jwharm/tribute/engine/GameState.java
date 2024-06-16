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
    private static final int REWARD_BONUS = 200;
    private int room, lives, score, startScore, bonus;
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
    }

    public void reset() {
        score = startScore;
        bonus = START_BONUS;
        this.dark = (room == 17);
    }

    public void die() {
        if (lives > 0)
            lives--;
    }

    public void roomCompleted() {
        score += bonus;
        startScore = score;
        room++;
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

    public void addBonusReward() {
        score += REWARD_BONUS;
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

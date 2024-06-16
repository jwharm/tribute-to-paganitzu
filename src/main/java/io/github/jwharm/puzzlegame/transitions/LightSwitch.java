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

package io.github.jwharm.puzzlegame.transitions;

import io.github.jwharm.puzzlegame.engine.Game;
import io.github.jwharm.puzzlegame.engine.Image;
import io.github.jwharm.puzzlegame.engine.Result;
import io.github.jwharm.puzzlegame.engine.Transition;
import io.github.jwharm.puzzlegame.ui.Messages;

import java.util.Set;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * Turns on the light in room 17.
 */
public class LightSwitch implements Transition {

    /*
     * First display message, then make light. Otherwise, the spiders are drawn
     * already while the message dialog is shown.
     */
    private boolean messageDisplayed = false;

    @Override
    public Result run(Game game) {
        if (!messageDisplayed) {
            game.state().showMessage(Messages.LIGHT_SWITCH_FOUND);
            messageDisplayed = true;
            return Result.CONTINUE;
        } else {
            game.state().makeLight();
            return Result.DONE;
        }
    }

    /**
     * Only this set of images is drawn in a dark level.
     */
    public static Set<Image> visibleInDark() {
        return Set.of(
                POOF_1, POOF_2, POOF_3, POOF_4, POOF_5, POOF_6,
                PLAYER_LEFT_MOVE, PLAYER_RIGHT_MOVE,
                PLAYER_LEFT_STAND, PLAYER_RIGHT_STAND,
                CRUMBLE_1, CRUMBLE_2, CRUMBLE_3, CRUMBLE_4, CRUMBLE_5,
                HIT_1, HIT_2, HIT_3, HIT_4, HIT_5, HIT_6, HIT_7
        );
    }
}

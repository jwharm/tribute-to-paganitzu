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

import io.github.jwharm.puzzlegame.engine.*;
import io.github.jwharm.puzzlegame.ui.Messages;

/**
 * Applies the curse of baldness in level 14. First, the "cursed" status is set,
 * and a message is displayed. Then, after the player moved a step to the right,
 * his hat falls off and a second message is displayed.
 */
public class Baldness implements Transition {

    private static final Position CURSED_SPOT = new Position(1, 11);
    private DropHat dropHatAnimation;
    private boolean dropped = false;

    /**
     * This transition must run before PlayerMove, so the animation of the
     * falling hat does not draw on top of the player.
     */
    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Result run(Game game) {
        Player player = game.room().player();

        // Apply curse & display message
        if (!player.cursed()) {
            player.curse();
            game.state().showMessage(Messages.CURSED);
        }

        // When moving player moves: Drop hat
        else if (!player.position().equals(CURSED_SPOT) && !dropped) {
            game.freeze();
            game.room().player().looseHat();
            if (dropHatAnimation == null)
                dropHatAnimation = new DropHat(game.room().get(CURSED_SPOT));

            Result result = dropHatAnimation.run(game);
            if (result == Result.DONE) {
                player.setDirection(Direction.LEFT);
                dropped = true;
            }
        }

        // After hat has dropped: Say "oops"
        else if (dropped) {
            game.state().showMessage(Messages.OOPS);
            game.unfreeze();
            return Result.DONE;
        }

        return Result.CONTINUE;
    }
}

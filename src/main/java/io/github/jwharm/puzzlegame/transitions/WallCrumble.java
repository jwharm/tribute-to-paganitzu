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

import java.util.List;

import static io.github.jwharm.puzzlegame.engine.Image.*;

/**
 * Some walls have hidden passages that will appear when the player pushes
 * against them. The WallCrumble transition will show a little animation,
 * remove the wall tile, and (depending on tile id) either expose a gem, or
 * display a message and reward the player with bonus points.
 */
public class WallCrumble extends Animation {

    private static final int DELAY = 1;
    private static final List<Image> IMAGES = List.of(CRUMBLE_3, CRUMBLE_4);
    private static final boolean LOOP = false;

    private final Tile tile;

    public WallCrumble(Tile tile) {
        super(DELAY, tile, IMAGES, LOOP);
        this.tile = tile;
    }

    @Override
    public Result run(Game game) {
        // Run the animation
        var result = super.run(game);

        if (result == Result.DONE) {
            game.unfreeze();

            // Tile 5 and 6 spawn a gem, 7 and 8 not
            switch (tile.id()) {
                case 5, 6 -> game.room().replace(tile, Tile.createGem());
                case 7, 8 -> game.room().remove(tile);
            }

            // Tile 8 displays message and assigns bonus (except in level 17)
            if (tile.id() == 8 && game.state().room() != 17) {
                game.state().showMessage(Messages.HIDDEN_AREA);
                game.state().addBonusReward();
            }
        }

        return result;
    }
}

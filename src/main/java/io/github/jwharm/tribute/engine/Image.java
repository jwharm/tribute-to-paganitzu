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

import io.github.jwharm.tribute.io.TileReader;

import java.util.NoSuchElementException;

/**
 * All images in the order that they are stored
 */
public enum Image {

    PLAYER_RIGHT_STAND(0),
    PLAYER_RIGHT_MOVE(1),
    PLAYER_LEFT_STAND(2),
    PLAYER_LEFT_MOVE(3),
    POOF_1(4),
    POOF_2(5),
    POOF_3(6),
    POOF_4(7),
    POOF_5(8),
    POOF_6(9),
    SNAKE_RIGHT_1(10),
    SNAKE_RIGHT_2(11),
    SNAKE_RIGHT_3(12),
    SNAKE_RIGHT_4(13),
    SNAKE_LEFT_1(14),
    SNAKE_LEFT_2(15),
    SNAKE_LEFT_3(16),
    SNAKE_LEFT_4(17),
    KEY(18),
    CRUMBLE_2(19),
    CRUMBLE_3(20),
    CRUMBLE_4(21),
    CRUMBLE_5(22),
    SKULL_RIGHT(23),
    SKULL_LEFT(24),
    FLASH(25),
    MUD(26),
    EMPTY(27),
    BOULDER(28),
    BOULDER_SPLASH(29),
    GEM_1(30),
    GEM_2(31),
    GEM_3(32),
    GEM_4(33),
    GEM_5(34),
    GEM_6(35),
    GEM_7(36),
    WALL_1(37),
    WALL_2(38),
    WALL_3(39),
    WALL_4(40),
    SPIDER_1(41),
    SPIDER_2(42),
    VENOM(43),
    VENOM_HIT_1(44),
    VENOM_HIT_2(45),
    CRUMBLE_1(46),
    WATER_5_1(47),
    WATER_5_2(48),
    WATER_7_1(49),
    WATER_7_2(50),
    WATER_9_1(51),
    WATER_9_2(52),
    WATER_8_1(53),
    WATER_8_2(54),
    WATER_1_1(55),
    WATER_1_2(56),
    WATER_3_1(57),
    WATER_3_2(58),
    WATER_2_1(59),
    WATER_2_2(60),
    WATER_4_1(61),
    WATER_4_2(62),
    WATER_6_1(63),
    WATER_6_2(64),
    PIPE_WALL_HORIZONTAL(65),
    PIPE_WALL_VERTICAL(66),
    PIPE_WALL_7(67),
    PIPE_WALL_9(68),
    PIPE_WALL_1(69),
    PIPE_WALL_3(70),
    PIPE_HORIZONTAL(71),
    PIPE_VERTICAL(72),
    PIPE_7(73),
    PIPE_9(74),
    PIPE_1(75),
    PIPE_3(76),
    PIPE_END_4_1(77),
    PIPE_END_4_2(78),
    PIPE_END_6_1(79),
    PIPE_END_6_2(80),
    PIPE_END_4_DRY(81),
    PIPE_END_6_DRY(82),
    PLAYER_NO_HAT_RIGHT_STAND(83),
    PLAYER_NO_HAT_RIGHT_MOVE(84),
    PLAYER_NO_HAT_LEFT_STAND(85),
    PLAYER_NO_HAT_LEFT_MOVE(86),
    FALLING_HAT_1(87),
    FALLING_HAT_2(88),
    FALLING_HAT_3(89),
    FALLING_HAT_4(90),
    LOCKED_DOOR(91),
    SPIKES(92),
    HIT_1(93),
    HIT_2(94),
    HIT_3(95),
    HIT_4(96),
    HIT_5(97),
    HIT_6(98),
    HIT_7(99),

    /**
     * Does not exist in the game data. Image is generated in
     * {@link TileReader#generateSpikeBarImage()}
     */
    SPIKE_BARS(100),

    /**
     * Does not exist in the game data. Image is generated in
     * {@link TileReader#generateArrowImage()}
     */
    ARROW(101);

    private final int id;

    public static Image of(int id) {
        for (var i : values()) if (i.id == id) return i;
        throw new NoSuchElementException();
    }

    public int id() {
        return id;
    }

    Image(int id) {
        this.id = id;
    }
}

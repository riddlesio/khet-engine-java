/*
 *  Copyright 2017 riddles.io (developers@riddles.io)
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 *      For the full copyright and license information, please view the LICENSE
 *      file that was distributed with this source code.
 */

package io.riddles.khet.game.board.piece;

import java.awt.*;

import io.riddles.khet.game.move.TurnType;

/**
 * io.riddles.khet.game.board.piece.PieceOrientation - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public enum KhetPieceOrientation {
    NORTH("N"),
    EAST("E"),
    SOUTH("S"),
    WEST("W");

    private final String shortHand;

    KhetPieceOrientation(String shortHand) {
        this.shortHand = shortHand;
    }

    public String toString() {
        return this.shortHand;
    }

    public static KhetPieceOrientation fromString(String input) {
        for (KhetPieceOrientation moveType : KhetPieceOrientation.values()) {
            if (moveType.toString().equalsIgnoreCase(input)) {
                return moveType;
            }
        }

        return null;
    }

    public KhetPieceOrientation turn(TurnType turnType) {
        switch (this) {
            case NORTH:
                if (turnType == TurnType.CLOCKWISE) return EAST;
                if (turnType == TurnType.COUNTERCLOCKWISE) return WEST;
            case EAST:
                if (turnType == TurnType.CLOCKWISE) return SOUTH;
                if (turnType == TurnType.COUNTERCLOCKWISE) return NORTH;
            case SOUTH:
                if (turnType == TurnType.CLOCKWISE) return WEST;
                if (turnType == TurnType.COUNTERCLOCKWISE) return EAST;
            case WEST:
                if (turnType == TurnType.CLOCKWISE) return NORTH;
                if (turnType == TurnType.COUNTERCLOCKWISE) return SOUTH;
        }

        return this;
    }

    public Point getDirection() {
        switch (this) {
            case NORTH:
                return new Point(0, -1);
            case EAST:
                return new Point(1, 0);
            case SOUTH:
                return new Point(0, 1);
            case WEST:
                return new Point(-1, 0);
        }

        return new Point(0, 0);
    }
}

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

/**
 * io.riddles.khet.game.board.piece.KhetPieceType - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public enum KhetPieceType {
    PHARAOH("PH"),
    SCARAB("SC"),
    PYRAMID("PY"),
    ANUBIS("AN"),
    SPHINX("SP");

    private final String shortHand;

    KhetPieceType(String shortHand) {
        this.shortHand = shortHand;
    }

    public String toString() {
        return this.shortHand;
    }

    public static KhetPieceType fromString(String shortHand) {
        String input = shortHand.toUpperCase();

        switch (input) {
            case "PH":
                return PHARAOH;
            case "SC":
                return SCARAB;
            case "PY":
                return PYRAMID;
            case "AN":
                return ANUBIS;
            case "SP":
                return SPHINX;
            default:
                return null;
        }
    }
}

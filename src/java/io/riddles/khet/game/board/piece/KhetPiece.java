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
 * io.riddles.khet.game.board.piece.KhetPiece - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetPiece {

    private KhetPieceType type;
    private int playerId;
    private KhetPieceOrientation orientation;

    KhetPiece(KhetPieceType type, int playerId, KhetPieceOrientation orientation) {
        this.type = type;
        this.playerId = playerId;
        this.orientation = orientation;
    }

    public static KhetPiece fromString(String string) {
        if (string.length() != 4) {
            return null;
        }

        KhetPieceType type = KhetPieceType.fromString(string.substring(0, 2));
        int playerId = Integer.parseInt(string.substring(2, 3));
        KhetPieceOrientation orientation = KhetPieceOrientation.fromString(string.substring(3));

        return new KhetPiece(type, playerId, orientation);
    }

    public void setOrientation(KhetPieceOrientation orientation) {
        this.orientation = orientation;
    }

    public KhetPieceType getType() {
        return this.type;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public KhetPieceOrientation getOrientation() {
        return this.orientation;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", this.type, this.playerId, this.orientation);
    }
}

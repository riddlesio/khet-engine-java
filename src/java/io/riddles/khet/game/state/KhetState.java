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

package io.riddles.khet.game.state;

import java.util.ArrayList;

import io.riddles.javainterface.game.state.AbstractState;
import io.riddles.khet.game.board.KhetBoard;

/**
 * io.riddles.khet.game.state.KhetState - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetState extends AbstractState<KhetPlayerState> {

    private KhetBoard board;

    // for initial state only
    public KhetState(ArrayList<KhetPlayerState> playerStates, KhetBoard board) {
        super(null, playerStates, 0);
        this.board = board;
    }

    public KhetState(KhetState previousState, ArrayList<KhetPlayerState> playerStates, int roundNumber) {
        super(previousState, playerStates, roundNumber);
        this.board = new KhetBoard(previousState.getBoard());
    }

    public KhetState createNextState(int roundNumber) {
        // Create new player states from current player states
        ArrayList<KhetPlayerState> playerStates = new ArrayList<>();
        for (KhetPlayerState playerState : this.getPlayerStates()) {
            playerStates.add(new KhetPlayerState(playerState.getPlayerId()));
        }

        // Create new state from current state
        return new KhetState(this, playerStates, roundNumber);
    }

    public void updatePlayerStates() {
        int[] playerPieceCount = this.board.countPlayerPieces();

        for (KhetPlayerState playerState : this.getPlayerStates()) {
            int pieceCount = playerPieceCount[playerState.getPlayerId()];
            playerState.setPieceCount(pieceCount);
        }
    }

    public KhetBoard getBoard() {
        return this.board;
    }
}

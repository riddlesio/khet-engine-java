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

package io.riddles.khet.game.processor;

import java.util.ArrayList;

import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.game.processor.SimpleProcessor;
import io.riddles.khet.engine.KhetEngine;
import io.riddles.khet.game.move.ActionType;
import io.riddles.khet.game.move.KhetMove;
import io.riddles.khet.game.move.KhetMoveDeserializer;
import io.riddles.khet.game.player.KhetPlayer;
import io.riddles.khet.game.state.KhetPlayerState;
import io.riddles.khet.game.state.KhetState;

/**
 * io.riddles.khet.game.processor.KhetProcessor - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetProcessor extends SimpleProcessor<KhetState, KhetPlayer> {

    private KhetMoveDeserializer moveDeserializer;
    private int winnerByOpponentError = -1;  // If a bot returns the wrong input, opponent wins

    public KhetProcessor(PlayerProvider<KhetPlayer> playerProvider) {
        super(playerProvider);
        this.moveDeserializer = new KhetMoveDeserializer();
    }

    @Override
    public boolean hasGameEnded(KhetState state) {
        int maxRounds = KhetEngine.configuration.getInt("maxRounds");
        ArrayList<Integer> noPharaohPlayerIds = state.getBoard().getNoPharaohPlayerIds();

        return  this.winnerByOpponentError >= 0 ||
                state.getRoundNumber() >= maxRounds ||
                noPharaohPlayerIds.size() > 0;
    }

    @Override
    public Integer getWinnerId(KhetState state) {
        if (this.winnerByOpponentError >= 0) {
            return this.winnerByOpponentError;
        }

        ArrayList<Integer> noPharaohPlayerIds = state.getBoard().getNoPharaohPlayerIds();

        if (noPharaohPlayerIds.size() == 1) {
            return 2 - (noPharaohPlayerIds.get(0) + 1);
        }

        return null;
    }

    @Override
    public double getScore(KhetState state) {
        return state.getRoundNumber();
    }

    @Override
    public KhetState createNextState(KhetState inputState, int roundNumber) {
        KhetState nextState = inputState;

        for (KhetPlayerState playerState : inputState.getPlayerStates()) {
            KhetPlayer player = getPlayer(playerState.getPlayerId());
            nextState = createNextStateForPlayer(nextState, player, roundNumber);

            if (hasGameEnded(nextState)) break;
        }

        return nextState;
    }

    private KhetState createNextStateForPlayer(KhetState inputState, KhetPlayer player, int roundNumber) {
        sendUpdatesToPlayer(inputState, player);
        KhetState movePerformedState = createMovePerformedState(inputState, player, roundNumber);
        movePerformedState.getBoard().dump();
        KhetState temp = createFireLaserState(movePerformedState, player, roundNumber);
        temp.getBoard().dump();
        System.err.println(temp.getBoard().getLaserPaths() + "\n\n");
        return temp;
    }

    /**
     * Creates the state after the player has performed his move
     * @param inputState State as it is before the move
     * @param player Player to perform the move
     * @param roundNumber Current round number
     * @return State after the move
     */
    private KhetState createMovePerformedState(KhetState inputState, KhetPlayer player, int roundNumber) {
        int playerId = player.getId();

        KhetState movePerformedState = inputState.createNextState(roundNumber);

        String response = player.requestMove(ActionType.MOVE);
        KhetMove move = this.moveDeserializer.traverse(response);

        movePerformedState.getBoard().processMove(move, playerId);

        if (move.getException() != null) {
            this.winnerByOpponentError = 2 - (playerId + 1);
        }

        KhetPlayerState playerState = movePerformedState.getPlayerStateById(playerId);
        playerState.setMove(move);
        movePerformedState.updatePlayerStates();

        return movePerformedState;
    }

    /**
     * Create state where the laser is fired and a piece might be destroyed
     * @param inputState State right after the move of a player
     * @param player Player whose laser will fire
     * @param roundNumber Current round number
     * @return State as it is after the laser has fired (laser still visible on board)
     */
    private KhetState createFireLaserState(KhetState inputState, KhetPlayer player, int roundNumber) {
        KhetState laserOnBoardState = inputState.createNextState(roundNumber);

        laserOnBoardState.getBoard().fireLaser(player.getId());
        laserOnBoardState.updatePlayerStates();

        return laserOnBoardState;
    }

    private void sendUpdatesToPlayer(KhetState state, KhetPlayer player) {
        player.sendUpdate("round", state.getRoundNumber());
        player.sendUpdate("board", state.getBoard().toString());

        for (KhetPlayerState targetPlayerState : state.getPlayerStates()) {
            KhetPlayer target = getPlayer(targetPlayerState.getPlayerId());
            player.sendUpdate("piece_count", target, targetPlayerState.getPieceCount());
        }
    }

    private KhetPlayer getPlayer(int id) {
        return this.playerProvider.getPlayerById(id);
    }
}

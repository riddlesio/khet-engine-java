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

package io.riddles.khet.engine;

import java.util.ArrayList;

import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.javainterface.engine.GameLoopInterface;
import io.riddles.javainterface.engine.SimpleGameLoop;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.io.IOInterface;
import io.riddles.khet.game.KhetSerializer;
import io.riddles.khet.game.board.KhetBoard;
import io.riddles.khet.game.player.KhetPlayer;
import io.riddles.khet.game.processor.KhetProcessor;
import io.riddles.khet.game.state.KhetPlayerState;
import io.riddles.khet.game.state.KhetState;

/**
 * io.riddles.khet.engine.KhetEngine - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetEngine extends AbstractEngine<KhetProcessor, KhetPlayer, KhetState> {

    public KhetEngine(PlayerProvider<KhetPlayer> playerProvider, IOInterface ioHandler) throws TerminalException {
        super(playerProvider, ioHandler);
    }

    @Override
    protected Configuration getDefaultConfiguration() {
        Configuration configuration = new Configuration();

        configuration.put("boardWidth", 10);
        configuration.put("boardHeight", 8);
        configuration.put("maxRounds", 100);

        return configuration;
    }

    @Override
    protected KhetProcessor createProcessor() {
        return new KhetProcessor(this.playerProvider);
    }

    @Override
    protected GameLoopInterface createGameLoop() {
        return new SimpleGameLoop();
    }

    @Override
    protected KhetPlayer createPlayer(int id) {
        return new KhetPlayer(id);
    }

    @Override
    protected void sendSettingsToPlayer(KhetPlayer player) {
        player.sendSetting("your_botid", player.getId());
        player.sendSetting("board_width", configuration.getInt("boardWidth"));
        player.sendSetting("board_height", configuration.getInt("boardHeight"));
        player.sendSetting("max_rounds", configuration.getInt("maxRounds"));
    }

    @Override
    protected KhetState getInitialState() {
        int width = configuration.getInt("boardWidth");
        int height = configuration.getInt("boardHeight");
        int playerCount = this.playerProvider.getPlayers().size();

        if (playerCount != 2) {
            throw new RuntimeException("This game requires exactly 2 players");
        }

        // Create initial board
        KhetBoard board = new KhetBoard(width, height, playerCount, getStartingBoardString());

        // Create intitial player states
        ArrayList<KhetPlayerState> playerStates = new ArrayList<>();
        for (KhetPlayer player : this.playerProvider.getPlayers()) {
            KhetPlayerState playerState = new KhetPlayerState(player.getId());
            playerStates.add(playerState);
        }

        // Create initial state
        KhetState state = new KhetState(playerStates, board);

        // Update initial player states
        state.updatePlayerStates();

        return state;
    }

    @Override
    protected String getPlayedGame(KhetState initialState) {
        KhetSerializer serializer = new KhetSerializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    private String getStartingBoardString() {
        return "0SP0S,1    ,.    ,.    ,.AN0S,.PH0S,.AN0S,.PY0E,0    ,1    ," +
               "0    ,.    ,.PY0S,.    ,.    ,.    ,.    ,.    ,.    ,1    ," +
               "0    ,.    ,.    ,.PY1E,.    ,.    ,.    ,.    ,.    ,1    ," +
               "0PY0N,.    ,.PY1S,.    ,.SC0N,.SC0E,.    ,.PY0E,.    ,1PY1W," +
               "0PY0E,.    ,.PY1W,.    ,.SC1E,.SC1N,.    ,.PY0N,.    ,1PY1S," +
               "0    ,.    ,.    ,.    ,.    ,.    ,.PY0E,.    ,.    ,1    ," +
               "0    ,.    ,.    ,.    ,.    ,.    ,.    ,.PY1N,.    ,1    ," +
               "0    ,1    ,.PY1S,.AN1N,.PH1N,.AN1N,.    ,.    ,0    ,1SP1N";
    }
}

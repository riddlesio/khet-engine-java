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

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Point;
import java.util.ArrayList;

import io.riddles.javainterface.game.state.AbstractStateSerializer;
import io.riddles.khet.game.board.KhetBoard;
import io.riddles.khet.game.move.KhetMoveSerializer;

/**
 * io.riddles.khet.game.state.KhetStateSerializer - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetStateSerializer extends AbstractStateSerializer<KhetState> {

    @Override
    public String traverseToString(KhetState state) {
        return visitState(state).toString();
    }

    @Override
    public JSONObject traverseToJson(KhetState state) {
        return visitState(state);
    }

    private JSONObject visitState(KhetState state) {
        KhetMoveSerializer moveSerializer = new KhetMoveSerializer();

        JSONObject stateObj = new JSONObject();
        KhetBoard board = state.getBoard();

        JSONArray players = new JSONArray();
        for (KhetPlayerState playerState : state.getPlayerStates()) {
            JSONObject playerObj = new JSONObject();
            playerObj.put("id", playerState.getPlayerId());
            playerObj.put("score", playerState.getPieceCount());

            if (playerState.getMove() != null) {
                playerObj.put("move", moveSerializer.traverseToJson(playerState.getMove()));
            } else {
                playerObj.put("move", JSONObject.NULL);
            }

            players.put(playerObj);
        }

        JSONArray laserPaths = new JSONArray();
        for (ArrayList<Point> laserPath : board.getLaserPaths()) {
            JSONArray laserPathObj = new JSONArray();

            for (Point point : laserPath) {
                laserPathObj.put(visitPoint(point));
            }

            laserPaths.put(laserPathObj);
        }

        stateObj.put("round", state.getRoundNumber());
        stateObj.put("board", board.toString());
        stateObj.put("lasers", laserPaths);

        return stateObj;
    }

    // TODO: move this to javainterface
    private JSONObject visitPoint(Point point) {
        JSONObject pointObj = new JSONObject();

        pointObj.put("x", point.x);
        pointObj.put("y", point.y);

        return pointObj;
    }
}

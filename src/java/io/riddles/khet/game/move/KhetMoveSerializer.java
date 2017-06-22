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

package io.riddles.khet.game.move;

import org.json.JSONObject;

import java.awt.*;

import io.riddles.javainterface.serialize.Serializer;

/**
 * io.riddles.khet.game.move.KhetMoveSerializer - Created on 13-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetMoveSerializer implements Serializer<KhetMove> {

    @Override
    public String traverseToString(KhetMove traversible) {
        return visitMove(traversible).toString();
    }

    @Override
    public JSONObject traverseToJson(KhetMove traversible) {
        return visitMove(traversible);
    }

    private JSONObject visitMove(KhetMove move) {
        JSONObject moveObj = new JSONObject();

        if (move.getMoveType() != null && move.getMoveType() != MoveType.NONE) {
            moveObj.put("moveType", move.getMoveType().toString());
        } else {
            moveObj.put("moveType", JSONObject.NULL);
        }

        if (move.getException() != null) {
            moveObj.put("exception", move.getException().getMessage());
        } else {
            moveObj.put("exception", JSONObject.NULL);
        }

        if (move instanceof KhetMoveMove) {
            moveObj.put("from", visitPoint(move.getFromCoordinate()));
            moveObj.put("to", visitPoint(((KhetMoveMove) move).getToCoordinate()));
        } else if (move instanceof KhetTurnMove) {
            moveObj.put("from", visitPoint(move.getFromCoordinate()));
            moveObj.put("turn", ((KhetTurnMove) move).getTurnType().toString());
        }

        return moveObj;
    }

    // TODO: move this to javainterface
    private JSONObject visitPoint(Point point) {
        JSONObject pointObj = new JSONObject();

        pointObj.put("x", point.x);
        pointObj.put("y", point.y);

        return pointObj;
    }
}

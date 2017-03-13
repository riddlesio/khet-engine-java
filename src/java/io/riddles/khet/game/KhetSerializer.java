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

package io.riddles.khet.game;

import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.javainterface.game.AbstractGameSerializer;
import io.riddles.khet.game.processor.KhetProcessor;
import io.riddles.khet.game.state.KhetState;
import io.riddles.khet.game.state.KhetStateSerializer;

/**
 * io.riddles.khet.game.KhetSerializer - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetSerializer extends AbstractGameSerializer<KhetProcessor, KhetState> {

    @Override
    public String traverseToString(KhetProcessor processor, KhetState initialState) {
        KhetStateSerializer stateSerializer = new KhetStateSerializer();
        JSONObject game = new JSONObject();

        game = addDefaultJSON(initialState, game, processor);

        JSONArray states = new JSONArray();
        states.put(stateSerializer.traverseToJson(initialState));

        KhetState state = initialState;
        while (state.hasNextState()) {
            state = (KhetState) state.getNextState();
            states.put(stateSerializer.traverseToJson(state));
        }

        game.put("states", states);

        return game.toString();
    }
}

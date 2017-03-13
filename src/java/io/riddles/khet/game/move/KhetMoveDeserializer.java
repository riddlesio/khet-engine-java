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

import java.awt.Point;

import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.javainterface.serialize.Deserializer;

/**
 * io.riddles.khet.game.move.KhetMoveDeserializer - Created on 9-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetMoveDeserializer implements Deserializer<KhetMove> {

    @Override
    public KhetMove traverse(String string) {
        try {
            return visitMove(string);
        } catch (InvalidInputException ex) {
            return new KhetMove(ex);
        } catch (Exception ex) {
            return new KhetMove(new InvalidInputException("Failed to parse move"));
        }
    }

    private KhetMove visitMove(String input) throws InvalidInputException {
        String[] split = input.split(" ");
        MoveType moveType = MoveType.fromString(split[0]);

        if (split.length != 3) {
            throw new InvalidInputException("Move doesn't split into 3 parts");
        }

        switch (moveType) {
            case MOVE:
                return visitMoveMove(split[1], split[2]);
            case TURN:
                return visitTurnMove(split[1], split[2]);
            default:
                throw new InvalidInputException(
                        String.format("Move type %s not recognized", split[0]));
        }
    }

    private KhetMove visitMoveMove(String fromString, String toString) throws InvalidInputException {
        Point fromCoordinate = visitCoordinate(fromString);
        Point toCoordinate = visitCoordinate(toString);

        return new KhetMoveMove(fromCoordinate, toCoordinate);
    }

    private KhetMove visitTurnMove(String fromString, String turnString) throws InvalidInputException {
        Point fromCoordinate = visitCoordinate(fromString);
        TurnType turnType = TurnType.fromString(turnString);

        if (turnType == null) {
            throw new InvalidInputException(String.format("Unknown turn type %s", turnString));
        }

        return new KhetTurnMove(fromCoordinate, turnType);
    }

    private Point visitCoordinate(String coordinate) throws InvalidInputException {
        try {
            String[] split = coordinate.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);

            return new Point(x, y);
        } catch (Exception ex) {
            throw new InvalidInputException(
                    String.format("Failed to parse coordinate %s", coordinate));
        }
    }
 }

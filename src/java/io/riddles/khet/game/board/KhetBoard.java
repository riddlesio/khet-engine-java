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

package io.riddles.khet.game.board;

import java.awt.Point;
import java.util.ArrayList;

import io.riddles.javainterface.exception.InvalidMoveException;
import io.riddles.javainterface.game.data.Board;
import io.riddles.khet.game.board.piece.KhetPiece;
import io.riddles.khet.game.board.piece.KhetPieceOrientation;
import io.riddles.khet.game.board.piece.KhetPieceType;
import io.riddles.khet.game.move.KhetMove;
import io.riddles.khet.game.move.KhetMoveMove;
import io.riddles.khet.game.move.KhetTurnMove;
import io.riddles.khet.game.move.TurnType;

/**
 * io.riddles.khet.game.board.KhetBoard - Created on 8-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class KhetBoard extends Board<KhetPiece> {

    private int playerCount;
    private String[][] boardLayout;  // Contains what the board looks like without pieces, doesn't change
    private ArrayList<ArrayList<Point>> laserPaths;

    public KhetBoard(int width, int height, int playerCount, String fieldInput) {
        super(width, height);
        initializeBoard(width, height, playerCount);
        this.setFieldsFromString(fieldInput);
    }

    public KhetBoard(KhetBoard board) {
        super(board.getWidth(), board.getHeight());
        initializeBoard(board.getWidth(), board.getHeight(), board.getPlayerCount());
        this.setFieldsFromString(board.toString());
    }

    private void initializeBoard(int width, int height, int playerCount) {
        this.playerCount = playerCount;
        this.fields = new KhetPiece[width][height];
        this.boardLayout = new String[width][height];
        this.laserPaths = new ArrayList<>();
    }

    /**
     * Creates comma separated String
     * @return String
     */
    public String toString() {
        String output = "";
        String connector = "";

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.fields[x][y] == null) {
                    output += connector + this.boardLayout[x][y];
                } else {
                    output += connector + this.boardLayout[x][y] + this.fields[x][y];
                }

                connector = ",";
            }
        }

        return output;
    }

    public void dump() {
        for (int y = 0; y < this.height; y++) {  // dump the board
            String line = "";
            for (int x = 0; x < this.width; x++) {
                String cell;

                if (this.fields[x][y] != null) {
                    cell = this.boardLayout[x][y] + this.fields[x][y] + " ";
                } else {
                    cell = this.boardLayout[x][y] + "     ";
                }

                line += cell;
            }
            System.err.println(line);
        }

        System.err.println();
    }

    @Override
    public void clear() {}

    @Override
    public void setFieldsFromString(String input) {
        String[] split = input.split(",");
        int x = 0;
        int y = 0;

        for (String fieldString : split) {
            fieldString = fieldString.trim();
            if (fieldString.length() != 5 && fieldString.length() != 1) {
                throw new RuntimeException(
                        String.format("Field string not of correct length '%s'", fieldString));
            }

            this.boardLayout[x][y] = fieldString.substring(0, 1);
            this.fields[x][y] = fieldFromString(fieldString.substring(1));

            if (++x == this.width) {
                x = 0;
                y++;
            }
        }
    }

    /**
     * Counts the amount of pieces each player has
     * @return Array with player id as index and values piece count
     */
    public int[] countPlayerPieces() {
        int[] playerPieces = new int[this.playerCount];
        for (int i = 0; i < this.playerCount; i++) {
            playerPieces[i] = 0;
        }

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                try {
                    KhetPiece piece = this.fields[x][y];
                    playerPieces[piece.getPlayerId()]++;
                } catch (Exception ignored) {}
            }
        }

        return playerPieces;
    }

    public ArrayList<Integer> getNoPharaohPlayerIds() {
        ArrayList<Integer> noPharaohPlayerIds = new ArrayList<>();
        for (int id = 0; id < this.playerCount; id++) {
            noPharaohPlayerIds.add(id, id);
        }

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                KhetPiece piece = this.fields[x][y];
                if (piece != null && piece.getType() == KhetPieceType.PHARAOH) {
                    noPharaohPlayerIds.remove(Integer.valueOf(piece.getPlayerId()));
                }
            }
        }

        return noPharaohPlayerIds;
    }

    public void processMove(KhetMove move, int playerId) {
        if (move.isInvalid()) return;

        KhetPiece piece;
        try {
            piece = getPieceFromMove(move, playerId);
        } catch (InvalidMoveException ex) {
            move.setException(ex);
            return;
        }

        if (move instanceof KhetMoveMove) {
            processMoveMove((KhetMoveMove) move, piece);
        } else if (move instanceof KhetTurnMove) {
            processTurnMove((KhetTurnMove) move, piece);
        }
    }

    public void fireLaser(int playerId) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                KhetPiece piece = this.fields[x][y];

                if (piece != null &&
                        piece.getType() == KhetPieceType.SPHINX &&
                        piece.getPlayerId() == playerId) {

                    ArrayList<Point> laserPath = new ArrayList<>();
                    Point base = new Point(x, y);
                    laserPath.add(base);

                    progressLaser(base, piece.getOrientation().getDirection(), laserPath);

                    this.laserPaths.add(laserPath);
                }
            }
        }
    }

    private KhetPiece getPieceFromMove(KhetMove move, int playerId) throws InvalidMoveException {
        KhetPiece piece = this.getFieldAt(move.getFromCoordinate());

        if (piece == null) {
            throw new InvalidMoveException("There is no piece at selected coordinate");
        }

        if (piece.getPlayerId() != playerId) {
            throw new InvalidMoveException("Selected piece is not your piece");
        }

        return piece;
    }

    private void processMoveMove(KhetMoveMove move, KhetPiece piece) {
        try {
            validateMoveMove(move, piece);
        } catch (InvalidMoveException ex) {
            move.setException(ex);
            return;
        }

        Point to = move.getToCoordinate();
        Point from = move.getFromCoordinate();
        KhetPiece oldSquare = this.fields[to.x][to.y];
        this.fields[to.x][to.y] = piece;
        this.fields[from.x][from.y] = oldSquare;
    }

    private void validateMoveMove(KhetMoveMove move, KhetPiece piece) throws InvalidMoveException {
        switch (piece.getType()) {
            case PHARAOH:
            case ANUBIS:
            case PYRAMID:
                validatePieceMove(move, piece, false);
                break;
            case SCARAB:
                validatePieceMove(move, piece, true);
                break;
            case SPHINX:
                throw new InvalidMoveException("Can't move a Sphinx piece");
            default:
                throw new RuntimeException("Unknown piece type");
        }
    }

    private void validatePieceMove(KhetMoveMove move, KhetPiece piece, boolean canSwitch) throws InvalidMoveException {
        Point to = move.getToCoordinate();
        Point from = move.getFromCoordinate();
        int opponentId = 2 - (piece.getPlayerId() + 1);

        if (Math.abs(to.x - from.x) > 1 || Math.abs(to.y - from.y) > 1) {
            throw new InvalidMoveException("Piece can only move to a neighboring square");
        }

        if (isOutsideBoard(to)) {
            throw new InvalidMoveException("Can't move a piece outside of the board");
        }

        if (this.boardLayout[to.x][to.y].equals(opponentId + "")) {
            throw new InvalidMoveException("Your piece can't move to this square");
        }

        KhetPiece occupying = this.fields[to.x][to.y];
        if (!canSwitch) {
            if (occupying != null) {
                throw new InvalidMoveException("This piece can't move to an occupied square");
            }
        } else {
            if (occupying != null &&
                    occupying.getType() != KhetPieceType.PYRAMID &&
                    occupying.getType() != KhetPieceType.ANUBIS) {
                throw new InvalidMoveException("You can't switch places with this piece");
            }
        }
    }

    private void processTurnMove(KhetTurnMove move, KhetPiece piece) {
        try {
            KhetPieceOrientation orientation = getNewOrientation(move, piece);
            piece.setOrientation(orientation);
        } catch (InvalidMoveException ex) {
            move.setException(ex);
        }
    }

    private KhetPieceOrientation getNewOrientation(KhetTurnMove move, KhetPiece piece) throws InvalidMoveException {
        KhetPieceOrientation orientation = piece.getOrientation().turn(move.getTurnType());

        if (piece.getType() != KhetPieceType.SPHINX) {
            return orientation;
        }

        Point from = move.getFromCoordinate();
        Point direction = orientation.getDirection();
        Point pieceFront = new Point(from.x + direction.x, from.y + direction.y);

        if (isOutsideBoard(pieceFront)) {
            throw new InvalidMoveException("The Sphinx can only be turned to face the board");
        }

        return orientation;
    }

    private void progressLaser(Point current, Point direction, ArrayList<Point> path) {
        Point next = new Point(current.x + direction.x, current.y + direction.y);

        if (isOutsideBoard(next)) return;

        KhetPiece piece = this.fields[next.x][next.y];

        if (piece == null) {
            path.add(next);
            progressLaser(next, direction, path);
            return;
        }

        path.add(next);
        hitPiece(piece, next, direction, path);
    }

    private void hitPiece(KhetPiece piece, Point next, Point direction, ArrayList<Point> path) {
        switch (piece.getType()) {
            case PHARAOH:
                this.fields[next.x][next.y] = null;
                break;
            case ANUBIS:
                Point opposite = getOppositeDirection(direction);
                if (!opposite.equals(piece.getOrientation().getDirection())) {
                    this.fields[next.x][next.y] = null;
                }
                break;
            case PYRAMID:
                hitPyramid(piece, next, direction, path);
                break;
            case SCARAB:
                hitScarab(piece, next, direction, path);
                break;
        }
    }

    private void hitPyramid(KhetPiece piece, Point next, Point direction, ArrayList<Point> path) {
        Point opposite = getOppositeDirection(direction);
        Point side = piece.getOrientation().turn(TurnType.COUNTERCLOCKWISE).getDirection();
        Point reflectedDirection;

        // hits head
        if (opposite.equals(piece.getOrientation().getDirection())) {
            reflectedDirection = reflectDirection(direction, TurnType.CLOCKWISE);
            progressLaser(next, reflectedDirection, path);

        // hits reflecting side
        } else if (direction.equals(side)) {
            reflectedDirection = reflectDirection(direction, TurnType.COUNTERCLOCKWISE);
            progressLaser(next, reflectedDirection, path);

        // hits back or non-reflecting side
        } else {
            this.fields[next.x][next.y] = null;
        }
    }

    private void hitScarab(KhetPiece piece, Point next, Point direction, ArrayList<Point> path) {
        Point opposite = getOppositeDirection(direction);
        Point reflectedDirection;

        // hits head or back
        if (opposite.equals(piece.getOrientation().getDirection()) ||
                direction.equals(piece.getOrientation().getDirection())) {
            reflectedDirection = reflectDirection(direction, TurnType.CLOCKWISE);

        // hits a side
        } else {
            reflectedDirection = reflectDirection(direction, TurnType.COUNTERCLOCKWISE);
        }

        progressLaser(next, reflectedDirection, path);
    }

    private Point getOppositeDirection(Point direction) {
        return new Point(direction.x * -1, direction.y * -1);
    }

    private Point reflectDirection(Point direction, TurnType turnType) {
        Point reflected = new Point(direction.y, direction.x);

        if (turnType == TurnType.CLOCKWISE) {
            reflected.y *= -1;
        } else {
            reflected.x *= -1;
        }

        return reflected;
    }

    private boolean isOutsideBoard(Point point) {
        return point.x < 0 || point.y < 0 || point.x >= this.width || point.y >= this.height;
    }

    private int getPlayerCount() {
        return this.playerCount;
    }

    public ArrayList<ArrayList<Point>> getLaserPaths() {
        return this.laserPaths;
    }

    @Override
    public KhetPiece fieldFromString(String field) {
        return KhetPiece.fromString(field);
    }
}

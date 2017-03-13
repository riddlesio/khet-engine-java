package io.riddles.khet.game.board

import io.riddles.javainterface.exception.InvalidMoveException
import io.riddles.khet.game.board.piece.KhetPieceOrientation
import io.riddles.khet.game.board.piece.KhetPieceType
import io.riddles.khet.game.move.KhetMoveMove
import io.riddles.khet.game.move.KhetTurnMove
import io.riddles.khet.game.move.TurnType
import spock.lang.Specification

import java.awt.Point

/**
 * io.riddles.khet.game.board.KhetBoardSpec - Created on 10-3-17
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
class KhetBoardSpec extends Specification {

    String initialBoard = "0SP0S,1    ,.    ,.    ,.AN0S,.PH0S,.AN0S,.PY0E,0    ,1    ," +
                          "0    ,.    ,.PY0S,.    ,.    ,.    ,.    ,.    ,.    ,1    ," +
                          "0    ,.    ,.    ,.PY1E,.    ,.    ,.    ,.    ,.    ,1    ," +
                          "0PY0N,.    ,.PY1S,.    ,.SC0N,.SC0E,.    ,.PY0E,.    ,1PY1W," +
                          "0PY0E,.    ,.PY1W,.    ,.SC1E,.SC1N,.    ,.PY0N,.    ,1PY1S," +
                          "0    ,.    ,.    ,.    ,.    ,.    ,.PY0E,.    ,.    ,1    ," +
                          "0    ,.    ,.    ,.    ,.    ,.    ,.    ,.PY1N,.    ,1    ," +
                          "0    ,1    ,.PY1S,.AN1N,.PH1N,.AN1N,.    ,.    ,0    ,1SP1N"

    def "test board serializing and deserializing"() {
        when:
        KhetBoard board = new KhetBoard(10, 8, 2, initialBoard)

        then:
        board.toString() == "0SP0S,1,.,.,.AN0S,.PH0S,.AN0S,.PY0E,0,1," +
                            "0,.,.PY0S,.,.,.,.,.,.,1," +
                            "0,.,.,.PY1E,.,.,.,.,.,1," +
                            "0PY0N,.,.PY1S,.,.SC0N,.SC0E,.,.PY0E,.,1PY1W," +
                            "0PY0E,.,.PY1W,.,.SC1E,.SC1N,.,.PY0N,.,1PY1S," +
                            "0,.,.,.,.,.,.PY0E,.,.,1," +
                            "0,.,.,.,.,.,.,.PY1N,.,1," +
                            "0,1,.PY1S,.AN1N,.PH1N,.AN1N,.,.,0,1SP1N"
    }

    def "test sphinx movements"() {
        setup:
        KhetBoard board = new KhetBoard(10, 8, 2, initialBoard)

        when:
        KhetMoveMove moveMove = new KhetMoveMove(new Point(0, 0), new Point(0, 1))
        KhetTurnMove turnMove1 = new KhetTurnMove(new Point(0, 0), TurnType.CLOCKWISE)
        KhetTurnMove turnMove2 = new KhetTurnMove(new Point(0, 0), TurnType.COUNTERCLOCKWISE)
        KhetTurnMove turnMove3 = new KhetTurnMove(new Point(0, 0), TurnType.COUNTERCLOCKWISE)
        board.processMove(moveMove, 0)
        board.processMove(turnMove1, 0)
        board.processMove(turnMove2, 0)
        board.processMove(turnMove3, 0)

        then:
        moveMove.getException() instanceof InvalidMoveException
        moveMove.getException().getMessage() == "Invalid move: Can't move a Sphinx piece"
        turnMove1.getException() instanceof InvalidMoveException
        turnMove1.getException().getMessage() == "Invalid move: The Sphinx can only be turned to face the board"
        turnMove2.getException() == null
        board.getFieldAt(new Point(0, 0)).getOrientation() == KhetPieceOrientation.EAST
        turnMove3.getException() instanceof InvalidMoveException
        turnMove3.getException().getMessage() == "Invalid move: The Sphinx can only be turned to face the board"
    }

    def "test scarab movements"() {
        setup:
        KhetBoard board = new KhetBoard(10, 8, 2, initialBoard)

        when:
        KhetTurnMove turnMove1 = new KhetTurnMove(new Point(4, 3), TurnType.CLOCKWISE)
        KhetTurnMove turnMove2 = new KhetTurnMove(new Point(4, 3), TurnType.COUNTERCLOCKWISE)
        KhetMoveMove moveMove1 = new KhetMoveMove(new Point(3, 3), new Point(3, 2))
        KhetMoveMove moveMove2 = new KhetMoveMove(new Point(4, 3), new Point(3, 2))
        KhetMoveMove moveMove3 = new KhetMoveMove(new Point(4, 3), new Point(4, 1))
        KhetMoveMove moveMove4 = new KhetMoveMove(new Point(4, 3), new Point(5, 3))
        KhetMoveMove moveMove5 = new KhetMoveMove(new Point(4, 3), new Point(3, 2))
        board.processMove(turnMove1, 0)
        board.processMove(turnMove2, 0)
        board.processMove(moveMove1, 0)
        board.processMove(moveMove2, 1)
        board.processMove(moveMove3, 0)
        board.processMove(moveMove4, 0)
        board.processMove(moveMove5, 0)

        then:
        turnMove1.getException() == null
        turnMove2.getException() == null
        moveMove1.getException() instanceof InvalidMoveException
        moveMove2.getException() instanceof InvalidMoveException
        moveMove3.getException() instanceof InvalidMoveException
        moveMove4.getException() instanceof InvalidMoveException
        moveMove5.getException() == null
        board.getFieldAt(new Point(3, 2)).getType() == KhetPieceType.SCARAB
        board.getFieldAt(new Point(3, 2)).getOrientation() == KhetPieceOrientation.NORTH
        board.getFieldAt(new Point(4, 3)).getType() == KhetPieceType.PYRAMID
    }

    def "test fire laser"() {
        when:
        KhetBoard board1 = new KhetBoard(10, 8, 2, initialBoard)
        board1.fireLaser(0)

        String input2 = "0SP0S,1    ,.    ,.    ,.AN0S,.PH0S,.AN0S,.PY0E,0    ,1    ," +
                        "0    ,.    ,.PY0S,.    ,.    ,.    ,.    ,.    ,.    ,1    ," +
                        "0    ,.    ,.PY1S,.PY1E,.    ,.    ,.    ,.    ,.    ,1    ," +
                        "0PY0N,.    ,.    ,.    ,.SC0N,.SC0E,.    ,.PY0E,.    ,1PY1W," +
                        "0PY0E,.    ,.PY1W,.    ,.SC1E,.SC1N,.    ,.PY0N,.    ,1PY1S," +
                        "0    ,.    ,.    ,.    ,.    ,.    ,.PY0E,.    ,.    ,1    ," +
                        "0    ,.    ,.    ,.    ,.    ,.    ,.    ,.PY1N,.    ,1    ," +
                        "0    ,1    ,.PY1S,.AN1N,.PH1N,.AN1N,.    ,.    ,0    ,1SP1N"
        KhetBoard board2 = new KhetBoard(10, 8, 2, input2)
        board2.fireLaser(0)

        then:
        // nothing changes when laser fires on initial board
        board1.toString() == "0SP0S,1,.,.,.AN0S,.PH0S,.AN0S,.PY0E,0,1," +
                            "0,.,.PY0S,.,.,.,.,.,.,1," +
                            "0,.,.,.PY1E,.,.,.,.,.,1," +
                            "0PY0N,.,.PY1S,.,.SC0N,.SC0E,.,.PY0E,.,1PY1W," +
                            "0PY0E,.,.PY1W,.,.SC1E,.SC1N,.,.PY0N,.,1PY1S," +
                            "0,.,.,.,.,.,.PY0E,.,.,1," +
                            "0,.,.,.,.,.,.,.PY1N,.,1," +
                            "0,1,.PY1S,.AN1N,.PH1N,.AN1N,.,.,0,1SP1N"
        board2.getFieldAt(new Point(2, 4)) == null
    }
}

package tictactoegame;

import board.*;
import io.BoardPrinter;
import io.DrawPrinter;
import io.InputNumberTaker;
import io.VictoryPrinter;
import player.Player;
import player.PlayerChanger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TicTacToeGame {
    private static final Integer MAX = 9;
    private static final Integer MIN_TO_CHECK = 5;

    private Integer iteration = 1;

    private final Board board = Board.createBoard();
    private final BoardPrinter boardPrinter = new BoardPrinter();
    private final PlayerChanger playerChanger = new PlayerChanger();
    private final List<Player> players = new ArrayList<>();

    private Player player;
    private Mark currentMark = Mark.O;

    private final EmptyPositionLister emptyLister = new EmptyPositionLister();
    private Set<Integer> emptyPositions = new TreeSet<>();

    private final InputNumberTaker inputNumberTaker = new InputNumberTaker();

    private final FieldAdder fieldAdder = new FieldAdder();


    private PossibleSequences currentSequence;
    private final List<PossibleSequences> possibleSequencesList = new ArrayList<>();
    private final SequenceRemover sequenceRemover = new SequenceRemover();

    private SequenceChanger sequenceChanger = new SequenceChanger();

    private final VictoryChecker victoryChecker = new VictoryChecker();

    public TicTacToeGame() {
        players.add( new Player("Player 1", Mark.O) );
        players.add( new Player("Player 2", Mark.X) );
        player = players.get(0);
        possibleSequencesList.add(PossibleSequences.createInitialSequences()); // for pl 0
        possibleSequencesList.add(PossibleSequences.createInitialSequences()); // for pl 1
        currentSequence = possibleSequencesList.get(1);
    }



    // check if continue playing
    private boolean checkPlay() {
        return iteration <= MAX;
    }

    // increment iteration
    private void increment() {
        ++iteration;
    }

    // change mark
    private Mark changeCurrentMark() {
        if (!player.checkMark( currentMark )) {
            return currentMark.changeMark();
        }
        return currentMark;
    }

    // return new position
    private Integer newPosition() {
        emptyPositions = emptyLister.listEmptyPositions(board);
        return inputNumberTaker.collectValidData(player, emptyPositions);
    }

    // add field
    private void addNewField(Integer newPosition) {
        fieldAdder.addField(board, newPosition, currentMark);
    }

    // print the board
    private void printBoard() {
        boardPrinter.printBoard(board);
    }

    // check the draw
    private void draw() {
        if (iteration == MAX) {
            new DrawPrinter().drawInfo();
        }
    }

    // change player
    private Player changePlayer() {
        return playerChanger.changePlayer(player, players.get(0), players.get(1));
    }

    // victory message
    private void victoryInfo(Player player) {
        new VictoryPrinter().printMessage( player );
    }

    // check if the minimum iterations for victory occurred
    private boolean possibleVictory() {
        return iteration >= MIN_TO_CHECK;
    }

    // come play the game :)
    public void play() throws IOException {
        // game main loop
        while (checkPlay()) {
            // change current mark
            currentMark = changeCurrentMark();
            // new position
            Integer newPosition = newPosition();
            // new field
            addNewField(newPosition);
            // print board
            printBoard();
            // remove sequences
            sequenceRemover.remove(currentSequence, newPosition);
            // change sequences
            currentSequence = sequenceChanger.changeSequences(currentSequence, possibleSequencesList.get(0), possibleSequencesList.get(1));
            // check victory
            if ( possibleVictory() && victoryChecker.checkVictory(board, currentSequence, currentMark) ) {
                victoryInfo( player );
                System.exit(0);
            }
            // check draw
            draw();
            //change current player
            player = changePlayer();
            //increment
            increment();
        }
    }
}

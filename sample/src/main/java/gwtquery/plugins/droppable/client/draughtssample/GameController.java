/*
 * Copyright 2010 The gwtquery plugins team.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gwtquery.plugins.droppable.client.draughtssample;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.droppable.client.draughtssample.CheckerBoard.CHECKERBOARD_SELECTOR;
import static gwtquery.plugins.droppable.client.draughtssample.DraughtsSample.EVENT_BUS;

import com.google.gwt.user.client.Timer;

import gwtquery.plugins.draggable.client.StopDragException;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent.DragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceCapturedEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceMoveEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PlayerChangeEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PlayerLostEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceMoveEvent.PieceMoveEventHandler;
import gwtquery.plugins.droppable.client.draughtssample.resources.DraughtsResources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains all code needed for the control of the game
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class GameController implements PieceMoveEventHandler,
    DragStartEventHandler, DragStopEventHandler {

  /**
   * Player type
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  public static enum Player {
    RED, WHITE;

    public String getKingClassName() {
      if (this == RED) {
        return DraughtsResources.INSTANCE.css().redKing();
      } else {
        return DraughtsResources.INSTANCE.css().whiteKing();
      }
    }

    public String getPieceClassName() {
      if (this == RED) {
        return DraughtsResources.INSTANCE.css().redPiece();
      } else {
        return DraughtsResources.INSTANCE.css().whitePiece();
      }
    }

    public int getYDirection() {
      return this == Player.RED ? 1 : -1;
    }
  }

  /**
   * Position of a piece in the checkerboard.
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  public static class Position {
    private int x;
    private int y;

    public Position(int column, int row) {
      x = column;
      y = row;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Position) || obj == null) {
        return false;
      }
      Position pos2 = (Position) obj;
      return pos2.x == x && pos2.y == y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    @Override
    public int hashCode() {
      return x ^ y;
    }

    public boolean isValid() {
      return x >= 0 && x < CheckerBoard.SQUARE_NUMBER && y >= 0
          && y < CheckerBoard.SQUARE_NUMBER;

    }

    public void setX(int x) {
      this.x = x;
    }

    public void setY(int y) {
      this.y = y;
    }

  }

  private static GameController INSTANCE = new GameController();

  public static GameController getInstance() {
    return INSTANCE;
  }

  private Player currentPlayer;
  private Timer currentTimer;
  private Map<Position, Piece> pieceByPosition;
  private boolean timerIsRunning;

  private GameController() {
    EVENT_BUS.addHandler(PieceMoveEvent.TYPE, this);
  }

  public Player getPlayerAt(Position position) {
    Piece pieceAtPosition = pieceByPosition.get(position);
    if (pieceAtPosition != null) {
      return pieceAtPosition.getPlayer();
    }
    return null;
  }

  public void onDragStart(DragStartEvent event) {
    Piece draggingPiece = (Piece) event.getDraggableWidget();

    if (draggingPiece.getPlayer() != currentPlayer) {
      // don't start the drag process it's not the right player !!
      throw new StopDragException();
    }

    if (currentTimer != null) {
      currentTimer.cancel();
      timerIsRunning = false;
    }

  }

  public void onDragStop(DragStopEvent event) {

    if (currentTimer != null && !timerIsRunning) {
      // we stopped the timer when the drag operation started and not jump is
      // occurred during this drag operation
      playerChange();
      currentTimer = null;
    }

  }

  public void onPieceMove(PieceMoveEvent event) {
    maybeStopTimer();

    Piece piece = event.getPiece();
    Position newPosition = event.getNewPosition();
    Position oldPosition = event.getOldPosition();

    pieceMove(piece, oldPosition, newPosition);

    if (maybeJumpingOccurred(piece, oldPosition, newPosition)
        && maybeMultipleJumpingIsPossible(piece)) {
      // other jump is possible, let one sec to do it
      startTimer();
    } else {
      playerChange();
    }

  }

  public void pieceMove(Piece piece, Position oldPosition, Position newPosition) {

    pieceByPosition.put(newPosition, piece);

    if (oldPosition != null) { // real move, not init phase
      pieceByPosition.remove(oldPosition);
      piece.setPosition(newPosition);
    }

  }

  public void reset() {
    pieceByPosition = new HashMap<Position, Piece>();
    currentPlayer = Player.RED;
  }

  public void restartGame() {
    getCheckerBoard().clear();
    reset();
    startGame();

  }

  public void startGame() {
    CheckerBoard checkerBoard = getCheckerBoard();
    checkerBoard.fillBoard();
    checkerBoard.lock();
    EVENT_BUS.fireEvent(new PlayerChangeEvent(currentPlayer));
    calculateNextMoves();
  }

  private boolean calculateNextMove(Piece currentPiece) {

    List<Position> nextPossibleMove = currentPiece.getPossibleMove();
    for (Position p : nextPossibleMove) {
      getCheckerBoard().authorizeMove(currentPiece, p);
    }
    return nextPossibleMove.size() > 0;
  }

  private boolean calculateNextMoves() {
    boolean hasNextMove = false;
    for (Piece currentPiece : pieceByPosition.values()) {
      if (currentPiece.getPlayer() != currentPlayer) {
        continue;
      }
      hasNextMove |= calculateNextMove(currentPiece);
    }

    if (!hasNextMove) {
      EVENT_BUS.fireEvent(new PlayerLostEvent(currentPlayer));
    }

    return hasNextMove;

  }

  private CheckerBoard getCheckerBoard() {
    return (CheckerBoard) $(CHECKERBOARD_SELECTOR).widget();

  }

  private boolean maybeJumpingOccurred(Piece piece, Position oldPosition,
      Position newPosition) {

    int oldPositionX = oldPosition.getX();
    int oldPositionY = oldPosition.getY();
    int newPositionX = newPosition.getX();
    int newPositionY = newPosition.getY();
    int maybeCapturedX = newPositionX - (newPositionX > oldPositionX ? 1 : -1);
    int maybeCapturedY = newPositionY - (newPositionY > oldPositionY ? 1 : -1);
    Position maybeCaptured = new Position(maybeCapturedX, maybeCapturedY);
    Player player = getPlayerAt(maybeCaptured);

    if (player != null && piece.getPlayer() != player) {
      Piece capturedPiece = pieceByPosition.remove(maybeCaptured);
      capturedPiece.die();
      EVENT_BUS.fireEvent(new PieceCapturedEvent(capturedPiece));
      return true;
    }
    return false;

  }

  private boolean maybeMultipleJumpingIsPossible(Piece p) {
    List<Position> nextJumps = p.getNextJumps();
    CheckerBoard cb = getCheckerBoard();

    cb.lock();
    if (!nextJumps.isEmpty()) {
      for (Position position : nextJumps) {
        cb.authorizeMove(p, position);
      }
      return true;

    }

    return false;

  }

  private void maybeStopTimer() {
    if (currentTimer != null) {
      currentTimer.cancel();
      currentTimer = null;
      timerIsRunning = false;
    }

  }

  private void playerChange() {
    tooglePlayer();
    getCheckerBoard().lock();
    if (calculateNextMoves()) {
      EVENT_BUS.fireEvent(new PlayerChangeEvent(currentPlayer));
    }
  }

  private void startTimer() {
    currentTimer = new Timer() {
      @Override
      public void run() {
        playerChange();
        currentTimer = null;
        timerIsRunning = false;
      }
    };
    // Wait 1 sec before to change the player...
    // This allows the multiple jump
    currentTimer.schedule(1000);
    timerIsRunning = true;

  }

  private void tooglePlayer() {
    if (currentPlayer == Player.RED) {
      currentPlayer = Player.WHITE;
    } else {
      currentPlayer = Player.RED;
    }

  }

}

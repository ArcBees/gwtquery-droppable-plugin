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

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.ui.HTML;

import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;
import gwtquery.plugins.droppable.client.draughtssample.GameController.Player;
import gwtquery.plugins.droppable.client.draughtssample.GameController.Position;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceKingedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class Piece extends DraggableWidget<HTML> {

  private boolean isKing = false;
  private Player player;
  private Position position;

  public Piece(Player player, Position initialPosition) {
    this.player = player;
    this.position = initialPosition;
    init();
  }

  public void die() {
    // use GQuery to fade out the piece
    $(this).fadeOut(300, new Function() {
      @Override
      public void f() {
        ((CheckerBoard) $(CHECKERBOARD_SELECTOR).widget()).getCell(
            position.getY(), position.getX()).clear();
        position = null;
      }
    });

  }

  /**
   * Calculate if jumping is possible
   * 
   * @return
   */
  public List<Position> getNextJumps() {
    List<Position> possibleJumps = new ArrayList<Position>();
    for (int yDirection : getYDirections()) {
      Position possibleLeftJump = checkJump(-1, yDirection);
      if (possibleLeftJump != null) {
        possibleJumps.add(possibleLeftJump);
      }
      Position possibleRightJump = checkJump(1, yDirection);
      if (possibleRightJump != null) {
        possibleJumps.add(possibleRightJump);
      }
    }
    return possibleJumps;
  }

  public Player getPlayer() {
    return player;
  }

  public Position getPosition() {
    return position;
  }

  public List<Position> getPossibleMove() {
    List<Position> next = new ArrayList<Position>();

    for (int yDirection : getYDirections()) {
      Position leftNextPosition = checkNextPosition(-1, yDirection);
      if (leftNextPosition != null) {
        next.add(leftNextPosition);
      }
      Position rightNextPosition = checkNextPosition(+1, yDirection);
      if (rightNextPosition != null) {
        next.add(rightNextPosition);
      }
    }

    return next;
  }

  public boolean isKing() {
    return isKing;
  }

  public void kingMe() {
    if (isKing) {
      return;
    }

    isKing = true;
    removeStyleName(player.getPieceClassName());
    addStyleName(player.getKingClassName());
    EVENT_BUS.fireEvent(new PieceKingedEvent(this));
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  protected void init() {
    initWidget(new HTML());
    setStyleName(player.getPieceClassName());
    // ensure that this div have an unique id because we will use id to
    // determine if a DroppableSquare accept or not a Piece
    getElement().setId("piece_" + getElement().hashCode());
    setupDraggable();
  }

  private Position checkJump(int xDirection, int yDirection) {
    int currentX = position.getX();
    int currentY = position.getY();

    Player playerAtNextPosition = GameController.getInstance().getPlayerAt(
        new Position(currentX + xDirection, currentY + yDirection));

    if (playerAtNextPosition != null && playerAtNextPosition != player) {
      Position jumpPosition = new Position(currentX + 2 * xDirection, currentY
          + 2 * yDirection);
      if (jumpPosition.isValid()
          && GameController.getInstance().getPlayerAt(jumpPosition) == null) {
        return jumpPosition;
      }
    }
    return null;
  }

  private Position checkNextPosition(int xDirection, int yDirection) {

    int currentX = position.getX();
    int currentY = position.getY();

    Position nextPosition = new Position(currentX + xDirection, currentY
        + yDirection);

    if (nextPosition.isValid()) {
      Player playerAtNextPosition = GameController.getInstance().getPlayerAt(
          nextPosition);
      if (playerAtNextPosition == null) {
        return nextPosition;
      } else {
        return checkJump(xDirection, yDirection);
      }
    }
    return null;
  }

  private int[] getYDirections() {
    if (isKing) {
      return new int[] { -1, 1 };
    }
    return new int[] { player.getYDirection() };
  }

  private void setupDraggable() {
    // revert the piece if this one is not dropped
    setRevert(RevertOption.ON_INVALID_DROP);
    // be sure that when the piece is dragging, it is in front
    setDraggingZIndex(100);
    // set the opacity of the piece during the drag
    setDraggingOpacity((float) 0.8);
    // the piece cannot be drag outside the checkerboard
    setContainment($(CHECKERBOARD_SELECTOR).widget());
    // set cursor in the middle of the piece
    setCursorAt(new CursorAt(25, 25, null, null));
    // set the cursor to use during the drag
    setDraggingCursor(Cursor.MOVE);
    // start the drag operation on mousedown
    setDistance(0);

    // register the GameController for dragStart and drag stop event
    GameController gc = GameController.getInstance();
    addDragStartHandler(gc);
    addDragStopHandler(gc);

  }

}

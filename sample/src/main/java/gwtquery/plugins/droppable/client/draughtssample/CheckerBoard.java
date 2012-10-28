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

import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.droppable.client.draughtssample.GameController.Player;
import gwtquery.plugins.droppable.client.draughtssample.GameController.Position;
import gwtquery.plugins.droppable.client.draughtssample.resources.Css;
import gwtquery.plugins.droppable.client.draughtssample.resources.DraughtsResources;

import java.util.ArrayList;
import java.util.List;

/**
 * Checker board widget.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class CheckerBoard extends Grid {

  private static final Css CSS = DraughtsResources.INSTANCE.css();
  private static final String CHECKERBOARD_ID = "checkerBoard";
  public static final String CHECKERBOARD_SELECTOR = "#checkerBoard";
  public static final int PIECE_NUMBER = 12;
  public static final int SQUARE_NUMBER = 8;
  
  private List<DroppableSquare> droppableSquareList = new ArrayList<DroppableSquare>();

  public CheckerBoard() {
    super(SQUARE_NUMBER, SQUARE_NUMBER);
    initBoard();
  }

  public void authorizeMove(final Piece currentPiece, Position p) {
    Widget w = getWidget(p.getY(), p.getX());

    assert w instanceof DroppableSquare;

    DroppableSquare square = (DroppableSquare) w;
    square.acceptPiece(currentPiece);

  }

  @Override
  public void clear() {
    for (DroppableSquare square : droppableSquareList) {
      square.clear();
    }
  }

  /**
   * reinit the board by putting all pieces.
   */
  public void fillBoard() {
    // start with pieces of Red player
    Player player = Player.RED;
    ArrayList<Piece> pieces = new ArrayList<Piece>();
    for (int row = 0; row < SQUARE_NUMBER;) {
      for (int column = (row + 1) % 2; column < SQUARE_NUMBER; column += 2) {
        Piece piece = new Piece(player, new Position(column, row));
        // we will fade in the piece after
        $(piece).hide();
        pieces.add(piece);
        getCell(row, column).add(piece);
        GameController.getInstance().pieceMove(piece, null,
            new Position(column, row));

      }
      row++;
      if (row == (SQUARE_NUMBER / 2) - 1) {
        // continue with pieces of white player
        row += 2;
        player = Player.WHITE;
      }
    }

    fadeIn(pieces);

  }

  public HasWidgets getCell(int row, int column) {
    return (HasWidgets) getWidget(row, column);
  }

  /**
   * disable drop functionality of all droppable square
   */
  public void lock() {
    for (DroppableSquare square : droppableSquareList) {
      square.lock();
    }

  }

  private void fadeIn(final List<Piece> pieces) {
    if (pieces.isEmpty()) {
      return;
    }

    Piece first = pieces.remove(0);
    $(first).fadeIn(100, new Function() {
      @Override
      public void f() {
        fadeIn(pieces);
      }
    });
  }

  private void initBoard() {
    setCellPadding(0);
    setCellSpacing(0);

    boolean isWhiteCell = true;

    for (int row = 0; row < SQUARE_NUMBER; row++) {
      for (int column = 0; column < SQUARE_NUMBER; column++) {
        Widget cell = null;
        if (!isWhiteCell) {
          // only black cell can contains a piece and so is droppable
          cell = new DroppableSquare(new Position(column, row));
          cell.addStyleName(CSS.blackCell());
          droppableSquareList.add((DroppableSquare) cell);
        } else {
          cell = new SimplePanel();
          cell.addStyleName(CSS.whiteCell());
        }
        setWidget(row, column, cell);
        isWhiteCell = !isWhiteCell;
      }

      isWhiteCell = !isWhiteCell;
    }

    addStyleName(CSS.checkerBoard());
    //set and id to retrieve it later thnaks to GwtQuery
    getElement().setId(CHECKERBOARD_ID);

  }

}

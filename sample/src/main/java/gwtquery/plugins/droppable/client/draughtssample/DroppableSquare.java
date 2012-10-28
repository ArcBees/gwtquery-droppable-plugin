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

import static gwtquery.plugins.droppable.client.draughtssample.CheckerBoard.SQUARE_NUMBER;
import static gwtquery.plugins.droppable.client.draughtssample.DraughtsSample.EVENT_BUS;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.draughtssample.GameController.Position;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceMoveEvent;
import gwtquery.plugins.droppable.client.draughtssample.resources.DraughtsResources;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class DroppableSquare extends DroppableWidget<SimplePanel> implements HasWidgets,
    DropEventHandler {

  /**
   * We will play with accept function to know if a square can accept a piece or
   * not !
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  private static class CellAcceptFunction implements AcceptFunction {

    Collection<String> acceptedPieceIds = new ArrayList<String>();

    public CellAcceptFunction() {
    }

    public boolean acceptDrop(DragAndDropContext context) {
      return acceptedPieceIds.contains(context.getDraggable().getId());
    }

    public void addPieceId(String... ids) {
      for (String id : ids) {
        acceptedPieceIds.add(id);
      }
    }

    public void reset() {
      acceptedPieceIds = new ArrayList<String>();
    }

  }

  public Position position;

  public DroppableSquare(Position p) {
    this.position = p;
    init();

  }

  public void acceptPiece(Widget piece) {
    enable();
    CellAcceptFunction f = (CellAcceptFunction) getAccept();
    f.addPieceId(piece.getElement().getId());
  }

  public void add(Widget w) {
    ((SimplePanel)getWidget()).add(w);
  }

  public void clear() {
    ((SimplePanel)getWidget()).clear();

  }

  public void enable() {
    setDisabled(false);
  }


  public Iterator<Widget> iterator() {
    return ((SimplePanel)getWidget()).iterator();
  }

  public void lock() {
    setDisabled(true);
    ((CellAcceptFunction) getAccept()).reset();
  }

  public void onDrop(DropEvent event) {
    final Piece draggingPiece = (Piece) event.getDraggableWidget();

    // as we use original widget for drag operation, clear top and left css
    // properties set during the drag operation before adding it
    draggingPiece.getElement().getStyle().setTop(0, Unit.PX);
    draggingPiece.getElement().getStyle().setLeft(0, Unit.PX);

    if (draggingPiece.getParent() != getWidget()) {
      add(draggingPiece);
      EVENT_BUS.fireEvent(new PieceMoveEvent(draggingPiece, position, draggingPiece
          .getPosition()));
      if (isKingLine()) {
        draggingPiece.kingMe();
      }

    }

  }

  public boolean remove(Widget w) {
    return ((SimplePanel)getWidget()).remove(w);
  }

  private void init() {
    
    initWidget(new SimplePanel());
    
    // setup drop
    setDroppableHoverClass(DraughtsResources.INSTANCE.css().hoverCell());
    setActiveClass(DraughtsResources.INSTANCE.css()
        .activeCell());
    setAccept(new CellAcceptFunction());
    setTolerance(DroppableTolerance.POINTER);
    addDropHandler(this);
    
  }

  private boolean isKingLine() {
    return position.getY() == 0 || position.getY() == SQUARE_NUMBER - 1;
  }
}

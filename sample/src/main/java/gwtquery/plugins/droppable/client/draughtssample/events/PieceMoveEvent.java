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
package gwtquery.plugins.droppable.client.draughtssample.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import gwtquery.plugins.droppable.client.draughtssample.Piece;
import gwtquery.plugins.droppable.client.draughtssample.GameController.Position;

/**
 * Event fired when a piece move on the checker board.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class PieceMoveEvent extends
    GwtEvent<PieceMoveEvent.PieceMoveEventHandler> {

  public interface PieceMoveEventHandler extends EventHandler {
    public void onPieceMove(PieceMoveEvent event);
  }

  public static Type<PieceMoveEventHandler> TYPE = new Type<PieceMoveEventHandler>();

  private Position newPosition;
  private Position oldPosition;
  private Piece piece;

  public PieceMoveEvent(Piece piece, Position newPosition, Position oldPosition) {
    this.piece = piece;
    this.newPosition = newPosition;
    this.oldPosition = oldPosition;
  }

  @Override
  public Type<PieceMoveEventHandler> getAssociatedType() {
    return TYPE;
  }

  public Position getNewPosition() {
    return newPosition;
  }

  public Position getOldPosition() {
    return oldPosition;
  }

  public Piece getPiece() {
    return piece;
  }

  @Override
  protected void dispatch(PieceMoveEventHandler handler) {
    handler.onPieceMove(this);
  }

}

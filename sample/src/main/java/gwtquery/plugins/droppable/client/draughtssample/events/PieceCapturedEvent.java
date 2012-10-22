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

/**
 * Event fired when a piece is captured
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class PieceCapturedEvent extends
    GwtEvent<PieceCapturedEvent.PieceEatedEventHandler> {

  public interface PieceEatedEventHandler extends EventHandler {
    public void onPieceEated(PieceCapturedEvent event);
  }

  public static Type<PieceEatedEventHandler> TYPE = new Type<PieceEatedEventHandler>();

  private Piece piece;

  public PieceCapturedEvent(Piece piece) {
    this.piece = piece;
  }

  @Override
  public Type<PieceEatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  public Piece getPiece() {
    return piece;
  }

  @Override
  protected void dispatch(PieceEatedEventHandler handler) {
    handler.onPieceEated(this);
  }

}

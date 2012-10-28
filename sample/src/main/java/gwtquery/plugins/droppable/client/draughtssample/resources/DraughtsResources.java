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
package gwtquery.plugins.droppable.client.draughtssample.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used for this example.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public interface DraughtsResources extends ClientBundle {

  public DraughtsResources INSTANCE = GWT.create(DraughtsResources.class);

  @Source("draughts.css")
  public Css css();

  @Source("red_king.gif")
  ImageResource redKing();

  @Source("red_king_th.gif")
  ImageResource redKingThumb();

  @Source("red_piece.gif")
  ImageResource redPiece();

  @Source("red_piece_th.gif")
  ImageResource redPieceThumb();

  @Source("white_king.gif")
  ImageResource whiteKing();

  @Source("white_king_th.gif")
  ImageResource whiteKingThumb();

  @Source("white_piece.gif")
  ImageResource whitePiece();

  @Source("white_piece_th.gif")
  ImageResource whitePieceThumb();

}

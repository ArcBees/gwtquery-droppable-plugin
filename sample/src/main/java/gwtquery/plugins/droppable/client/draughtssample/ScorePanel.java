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

import static gwtquery.plugins.droppable.client.draughtssample.CheckerBoard.PIECE_NUMBER;
import static gwtquery.plugins.droppable.client.draughtssample.DraughtsSample.EVENT_BUS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.droppable.client.draughtssample.GameController.Player;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceCapturedEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceKingedEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PlayerChangeEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PlayerLostEvent;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceCapturedEvent.PieceEatedEventHandler;
import gwtquery.plugins.droppable.client.draughtssample.events.PieceKingedEvent.PieceKingedEventHandler;
import gwtquery.plugins.droppable.client.draughtssample.events.PlayerChangeEvent.PlayerChangeEventHandler;
import gwtquery.plugins.droppable.client.draughtssample.events.PlayerLostEvent.PlayerLostEventHandler;

/**
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class ScorePanel extends Composite implements PlayerChangeEventHandler,
    PieceEatedEventHandler, PieceKingedEventHandler, PlayerLostEventHandler {

  interface ScorePanelUiBinder extends UiBinder<Widget, ScorePanel> {
  }

  private static ScorePanelUiBinder uiBinder = GWT
      .create(ScorePanelUiBinder.class);
  
  @UiField
  Label infoMessage;
  @UiField
  Label redKingNbrLabel;
  @UiField
  Label redPieceNbrLabel;
  @UiField
  Button restartButton;
  @UiField
  Label whiteKingNbrLabel;
  @UiField
  Label whitePieceNbrLabel;
  
  private int redKingNbr;
  private int redPieceNbr;
  private int whiteKingNbr;
  private int whitePieceNbr;

  public ScorePanel() {
    initWidget(uiBinder.createAndBindUi(this));
    init();
    bind();

  }

  public void onPieceEated(PieceCapturedEvent event) {
    Piece eatedPiece = event.getPiece();
    if (eatedPiece.getPlayer() == Player.RED) {
      if (eatedPiece.isKing()) {
        redKingNbr--;
        redKingNbrLabel.setText("" + redKingNbr);
      } else {
        redPieceNbr--;
        redPieceNbrLabel.setText("" + redPieceNbr);
      }
    } else {
      if (eatedPiece.isKing()) {
        whiteKingNbr--;
        whiteKingNbrLabel.setText("" + whiteKingNbr);
      } else {
        whitePieceNbr--;
        whitePieceNbrLabel.setText("" + whitePieceNbr);
      }

    }

  }

  public void onPieceKinged(PieceKingedEvent event) {
    if (event.getPiece().getPlayer() == Player.RED) {
      redKingNbr++;
      redPieceNbr--;
      redKingNbrLabel.setText("" + redKingNbr);
      redPieceNbrLabel.setText("" + redPieceNbr);
    } else {
      whiteKingNbr++;
      whitePieceNbr--;
      whiteKingNbrLabel.setText("" + whiteKingNbr);
      whitePieceNbrLabel.setText("" + whitePieceNbr);
    }

  }

  public void onPlayerChange(PlayerChangeEvent event) {
    infoMessage.setText(event.getPlayer() + " player is playing");
  }

  public void onPlayerLost(PlayerLostEvent event) {
    infoMessage.setText("GAME OVER. Player " + event.getPlayer() + " lost.");

  }

  @UiHandler(value = { "restartButton" })
  public void onRestartButtonClicked(ClickEvent e) {
    GameController.getInstance().restartGame();
    init();
  }

  private void bind() {
    EVENT_BUS.addHandler(PlayerChangeEvent.TYPE, this);
    EVENT_BUS.addHandler(PieceCapturedEvent.TYPE, this);
    EVENT_BUS.addHandler(PieceKingedEvent.TYPE, this);
    EVENT_BUS.addHandler(PlayerLostEvent.TYPE, this);
  }

  private void init() {
    whitePieceNbr = PIECE_NUMBER;
    redPieceNbr = PIECE_NUMBER;
    whiteKingNbr = 0;
    redKingNbr = 0;
    redKingNbrLabel.setText("" + redKingNbr);
    redPieceNbrLabel.setText("" + redPieceNbr);
    whiteKingNbrLabel.setText("" + whiteKingNbr);
    whitePieceNbrLabel.setText("" + whitePieceNbr);

  }
}

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

import gwtquery.plugins.droppable.client.draughtssample.GameController.Player;

/**
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class PlayerChangeEvent extends
    GwtEvent<PlayerChangeEvent.PlayerChangeEventHandler> {

  public interface PlayerChangeEventHandler extends EventHandler {
    public void onPlayerChange(PlayerChangeEvent event);
  }

  public static Type<PlayerChangeEventHandler> TYPE = new Type<PlayerChangeEventHandler>();

  private Player player;

  public PlayerChangeEvent(Player player) {
    this.player = player;
  }

  @Override
  public Type<PlayerChangeEventHandler> getAssociatedType() {
    return TYPE;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  @Override
  protected void dispatch(PlayerChangeEventHandler handler) {
    handler.onPlayerChange(this);
  }

}

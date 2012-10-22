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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootPanel;

import gwtquery.plugins.droppable.client.draughtssample.resources.DraughtsResources;

/**
 * Checker sample entry point !
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class DraughtsSample implements EntryPoint {

  public static EventBus EVENT_BUS = new SimpleEventBus();

  public void onModuleLoad() {
    DraughtsResources.INSTANCE.css().ensureInjected();

    RootPanel.get("draughts").add(new CheckerBoard());
    RootPanel.get("score").add(new ScorePanel());

    GameController gameController = GameController.getInstance();
    gameController.reset();
    gameController.startGame();
  }

}

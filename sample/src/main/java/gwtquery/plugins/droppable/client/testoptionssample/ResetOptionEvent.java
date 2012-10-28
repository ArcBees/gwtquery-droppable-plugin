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
package gwtquery.plugins.droppable.client.testoptionssample;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * Event fired when the user click on the reset button
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class ResetOptionEvent extends
    GwtEvent<ResetOptionEvent.ResetOptionEventHandler> {

  public interface ResetOptionEventHandler extends EventHandler {
    public void onResetOption(ResetOptionEvent event);
  }

  public static Type<ResetOptionEventHandler> TYPE = new Type<ResetOptionEventHandler>();

  private Widget optionsPanel;

  public ResetOptionEvent(Widget optionsPanel) {
    this.optionsPanel = optionsPanel;
  }

  @Override
  public Type<ResetOptionEventHandler> getAssociatedType() {
    return TYPE;
  }

  public Widget getOptionsPanel() {
    return optionsPanel;
  }

  @Override
  protected void dispatch(ResetOptionEventHandler handler) {
    handler.onResetOption(this);
  }

}

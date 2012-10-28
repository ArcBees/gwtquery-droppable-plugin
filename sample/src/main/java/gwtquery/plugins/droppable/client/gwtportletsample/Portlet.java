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
package gwtquery.plugins.droppable.client.gwtportletsample;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

/**
 * Portlet widget
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class Portlet extends DraggableWidget<Widget> {

  interface PortletUiBinder extends UiBinder<Widget, Portlet> {
  }

  /**
   * This handler will modify the position css attribute of portlets during the
   * drag
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  private static class DraggablePositionHandler implements
      BeforeDragStartEventHandler, DragStopEventHandler {

    /**
     * before that the drag operation starts, we will "visually" detach the draggable by setting
     * it css position to absolute. 
     */
    public void onBeforeDragStart(BeforeDragStartEvent event) {
       // "detach" visually the element of the parent
      $(event.getDraggable()).css("position", "absolute");

    }

    public void onDragStop(DragStopEvent event) {
      // "reattach" the element
      $(event.getDraggable()).css("position", "relative").css("top", null).css(
          "left", null);

    }
  }

  // This handler is stateless
  private static DraggablePositionHandler HANDLER = new DraggablePositionHandler();
  private static PortletUiBinder uiBinder = GWT.create(PortletUiBinder.class);

  @UiField
  DivElement content;
  @UiField
  DivElement header;

  public Portlet(String header, String content) {
    initWidget(uiBinder.createAndBindUi(this));
    setup();
    setHeader(header);
    setContent(content);
  }

  public void setContent(String content) {
    this.content.setInnerText(content);
  }

  public void setHeader(String header) {
    this.header.setInnerText(header);
  }

  private void setup() {
    // opacity of the portlet during the drag
    setDraggingOpacity(new Float(0.8));
    // zIndex of the portlet during the drag
    setDraggingZIndex(1000);
    // add position handler
    addBeforeDragHandler(HANDLER);
    addDragStopHandler(HANDLER);

  }
}

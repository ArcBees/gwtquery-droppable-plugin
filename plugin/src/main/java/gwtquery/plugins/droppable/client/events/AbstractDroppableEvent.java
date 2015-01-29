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
package gwtquery.plugins.droppable.client.events;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.droppable.client.gwt.DragAndDropCellWidgetUtils.VALUE_KEY;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

import gwtquery.plugins.draggable.client.events.DragContext;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

/**
 * Abstract class for all droppable event
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 * @param <H>
 */
public abstract class AbstractDroppableEvent<H extends EventHandler> extends
    GwtEvent<H> {

  private DragAndDropContext dragAndDropContext;

  public AbstractDroppableEvent(DragAndDropContext context) {
    this.dragAndDropContext = context;
  }

  public AbstractDroppableEvent(Element droppable, DragContext ctx) {
    this.dragAndDropContext = new DragAndDropContext(ctx, droppable);
  }

  public DragAndDropContext getDragDropContext() {
    return dragAndDropContext;
  }

  /**
   * 
   * @return the current draggable DOM element
   */
  public Element getDraggable() {
    assert dragAndDropContext != null : "DragAndDropContext cannot be null";

    return dragAndDropContext.getDraggable();

  }

  /**
   * This method allows getting the data object linked to the draggable element
   * (a cell) in the context of CellWidget. It returns the data object being
   * rendered by the dragged cell. Return null if we are not in the context of
   * an drag and drop cell widget.
   * 
   * @param <T>
   *          the class of the data
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> T getDraggableData() {
    return (T) $(getDraggable()).data(VALUE_KEY);
  }

  /**
   * This method return the widget associated to the dragged DOM element if it
   * exist. It returns null otherwise.
   * 
   */
  public DraggableWidget<?> getDraggableWidget() {
    assert dragAndDropContext != null : "DragAndDropContext cannot be null";
    return dragAndDropContext.getDraggableWidget();
  }

  /**
   * 
   * @return the DOM element used for dragging display
   */
  public Element getDragHelper() {
    assert dragAndDropContext != null : "DragAndDropContext cannot be null";
    return dragAndDropContext.getHelper();
  }

  /**
   * 
   * @return the droppable DOM element
   */
  public Element getDroppable() {
    assert dragAndDropContext != null : "DragAndDropContext cannot be null";
    return dragAndDropContext.getDroppable();

  }

  /**
   * This method allows getting the data object linked to the droppable cell in
   * the context of CellWidget. It returns the data object being rendered by the
   * droppable cell. Return null if we are not in the context of an drag and
   * drop cell widget.
   * 
   * @param <T>
   *          the class of the data
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> T getDroppableData() {
    return (T) $(getDroppable()).data(VALUE_KEY);

  }

  /**
   * This method return the widget associated to the droppable DOM element if it
   * exist. It returns null otherwise.
   * 
   */
  public DroppableWidget<?> getDroppableWidget() {
    assert dragAndDropContext != null : "DragAndDropContext cannot be null";
    return dragAndDropContext.getDroppableWidget();
  }
  
  /**
   *
   * @return the list of selected draggables.
   */
  public List<Element> getSelectedDraggables() {
    assert dragAndDropContext != null : "DragAndDropContext cannot be null";
    return dragAndDropContext.getSelectedDraggables();
  }

  /**
   * @return the draggable element that initiate the drag operation (i.e. the
   *         clicked element)
   */
  public Element getInitialDraggable() {
    assert dragAndDropContext != null : "DragAndDropContext cannot be null";
    return dragAndDropContext.getInitialDraggable();
  }

  
  
}

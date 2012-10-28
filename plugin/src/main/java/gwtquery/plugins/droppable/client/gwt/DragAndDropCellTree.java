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
package gwtquery.plugins.droppable.client.gwt;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTreeNodeView;
import com.google.gwt.view.client.TreeViewModel;

import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStartEvent.DragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent.ActivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent.DeactivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;

/**
 * Implementation of the {@link CellTree} allowing dragging and dropping of the
 * tree node by using {@link DragAndDropNodeInfo}
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class DragAndDropCellTree extends CellTree {

  /**
   * Construct a new {@link CellTree}.
   * 
   * @param <T>
   *          the type of data in the root node
   * @param viewModel
   *          the {@link TreeViewModel} that backs the tree
   * @param rootValue
   *          the hidden root value of the tree
   */
  public <T> DragAndDropCellTree(TreeViewModel viewModel, T rootValue) {
    super(viewModel, rootValue);
  }

  /**
   * Construct a new {@link CellTree}.
   * 
   * @param <T>
   *          the type of data in the root node
   * @param viewModel
   *          the {@link TreeViewModel} that backs the tree
   * @param rootValue
   *          the hidden root value of the tree
   * @param resources
   *          the resources used to render the tree
   */
  public <T> DragAndDropCellTree(TreeViewModel viewModel, T rootValue,
      Resources resources) {
    super(viewModel, rootValue, resources);
  }

  protected <T> CellTreeNodeView<T> createTreeNodeView(T rootValue) {
    return new DragAndDropCellTreeNodeView<T>(this, null, null, getElement(),
        rootValue);
  }

  
  /*
   * Change for drag and drop
   */
  public boolean isLeaf(Object value) {
    return super.isLeaf(value);

  }

  public <T> TreeViewModel.NodeInfo<?> getNodeInfo(T value) {
    return super.getNodeInfo(value);
  }

  public boolean isKeyboardSelectionDisabled() {
    return super.isKeyboardSelectionDisabled();
  }

  private EventBus dragAndDropHandlerManager;

  protected final <H extends EventHandler> HandlerRegistration addDragAndDropHandler(
      H handler, Type<H> type) {
    return ensureDragAndDropHandlers().addHandler(type, handler);
  }

  protected EventBus ensureDragAndDropHandlers() {

    return dragAndDropHandlerManager == null ? dragAndDropHandlerManager = new SimpleEventBus()
        : dragAndDropHandlerManager;
  }

  /**
   * Add a handler object that will manage the {@link BeforeDragStartEvent}
   * event. this kind of event is fired before the initialization of the drag
   * operation.
   */
  public HandlerRegistration addCellBeforeDragHandler(
      BeforeDragStartEventHandler handler) {
    return addDragAndDropHandler(handler, BeforeDragStartEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DragEvent} event. this
   * kind of event is fired while a cell is being dragged
   */
  public HandlerRegistration addDragHandler(DragEventHandler handler) {
    return addDragAndDropHandler(handler, DragEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DragStartEvent} event.
   * This kind of event is fired when the drag operation starts.
   */
  public HandlerRegistration addDragStartHandler(DragStartEventHandler handler) {
    return addDragAndDropHandler(handler, DragStartEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DragStopEvent} event. This
   * kind of event is fired when the drag operation stops.
   */
  public HandlerRegistration addDragStopHandler(DragStopEventHandler handler) {
    return addDragAndDropHandler(handler, DragStopEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link ActivateDroppableEvent}
   * event. This kind of event is fired each time a droppable cell is activated.
   */
  public HandlerRegistration addActivateDroppableHandler(
      ActivateDroppableEventHandler handler) {
    return addDragAndDropHandler(handler, ActivateDroppableEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DeactivateDroppableEvent}
   * event. This kind of event is fired each time a droppable cell is
   * deactivated.
   */
  public HandlerRegistration addDeactivateDroppableHandler(
      DeactivateDroppableEventHandler handler) {
    return addDragAndDropHandler(handler, DeactivateDroppableEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DropEvent} event. This
   * kind of event is fired when an acceptable draggable is drop on a droppable
   * cell.
   */
  public HandlerRegistration addDropHandler(DropEventHandler handler) {
    return addDragAndDropHandler(handler, DropEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link OutDroppableEvent} event.
   * This kind of event is fired when an acceptable draggable is being dragged
   * out of a droppable cell.
   */
  public HandlerRegistration addOutDroppableHandler(
      OutDroppableEventHandler handler) {
    return addDragAndDropHandler(handler, OutDroppableEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link OverDroppableEvent} event.
   * This kind of event is fired when an acceptable draggable is being dragged
   * over a droppable cell.
   */
  public HandlerRegistration addOverDroppableHandler(
      OverDroppableEventHandler handler) {
    return addDragAndDropHandler(handler, OverDroppableEvent.TYPE);
  }

}

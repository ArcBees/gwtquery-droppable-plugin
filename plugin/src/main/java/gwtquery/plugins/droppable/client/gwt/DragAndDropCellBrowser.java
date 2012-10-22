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

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.query.client.Function;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.TreeViewModel;
import com.google.gwt.view.client.TreeViewModel.NodeInfo;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStartEvent.DragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.droppable.client.DroppableOptions;
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

import java.util.List;

/**
 * This cell browser allows you to define draggable and or droppable cells by
 * using {@link DragAndDropNodeInfo}.
 * 
 */
public class DragAndDropCellBrowser extends CellBrowser {

  protected class DragAndDropBrowserCellList<T> extends BrowserCellList<T> {

    /**
     * {@link CellDragAndDropBehaviour} used to determine if cells have to be
     * droppable and/or draggable
     */
    private CellDragAndDropBehaviour<T> cellDragAndDropBehaviour;

    /**
     * The options used for draggable cells.
     */
    private DraggableOptions draggableOptions;

    /**
     * The options used for droppable cells.
     */
    private DroppableOptions droppableOptions;

    public DragAndDropBrowserCellList(final Cell<T> cell, int level,
        ProvidesKey<T> keyProvider) {
      super(cell, level, keyProvider);
    }

    /**
     * 
     * @return the {@link CellDragAndDropBehaviour}
     */
    public CellDragAndDropBehaviour<T> getCellDragAndDropBehaviour() {
      return cellDragAndDropBehaviour;
    }

    /**
     * 
     * @return the {@link DraggableOptions} used to make cells draggable
     */
    public DraggableOptions getDraggableOptions() {
      return draggableOptions;
    }

    /**
     * 
     * @return the {@link DroppableOptions} used to make cells droppable
     */
    public DroppableOptions getDroppableOptions() {
      return droppableOptions;
    }

    /**
     * Set the {@link CellDragAndDropBehaviour}. If null is given, all cells
     * will be draggable and droppable
     * 
     * @param cellDragAndDropBehaviour
     */
    public void setCellDragAndDropBehaviour(
        CellDragAndDropBehaviour<T> cellDragAndDropBehaviour) {
      this.cellDragAndDropBehaviour = cellDragAndDropBehaviour;
    }

    /**
     * Set the {@link DraggableOptions} used to make cells draggable
     */
    public void setDraggableOptions(DraggableOptions draggableOptions) {
      this.draggableOptions = draggableOptions;
    }

    /**
     * Set the {@link DroppableOptions} used to make cells droppable
     */
    public void setDroppableOptions(DroppableOptions droppableOptions) {
      this.droppableOptions = droppableOptions;
    }

    protected void addDragAndDropBehaviour(List<T> values, int start) {

      int end = start + values.size();

      for (int rowIndex = start; rowIndex < end; rowIndex++) {
        T value = values.get(rowIndex - start);
        Element newCell = getRowElement(rowIndex);

        DragAndDropCellWidgetUtils.get().maybeMakeDraggableOrDroppable(newCell,
            value, cellDragAndDropBehaviour, draggableOptions,
            droppableOptions, ensureDragAndDropHandlers());
      }

    }

    protected final <H extends EventHandler> HandlerRegistration addDragAndDropHandler(
        H handler, Type<H> type) {
      return ensureDragAndDropHandlers().addHandler(type, handler);
    }

    protected void cleanAllCells() {
      $(getChildContainer()).children().each(new Function() {
        @Override
        public void f(Element div) {
          DragAndDropCellWidgetUtils.get().cleanCell(div);
        }
      });

    }

    @Override
    protected void onUnload() {

      cleanAllCells();
      super.onUnload();
    }

    @Override
    protected void replaceAllChildren(List<T> values, SafeHtml html) {
      // first clean old cell before remove it
      cleanAllCells();

      // lets the super class replace all child
      super.replaceAllChildren(values, html);

      // make the new cell draggable or droppable
      addDragAndDropBehaviour(values, 0);

    }

    @Override
    protected void replaceChildren(List<T> values, int start, SafeHtml html) {
      // clean cell has being replaced
      int end = start + values.size();
      for (int rowIndex = start; rowIndex < end; rowIndex++) {
        Element oldCell = getRowElement(rowIndex);
        DragAndDropCellWidgetUtils.get().cleanCell(oldCell);
      }

      // lets the super class replace all child
      super.replaceChildren(values, start, html);

      // make the new cell draggable or droppable
      addDragAndDropBehaviour(values, start);

    }

  }

  private EventBus dragAndDropHandlerManager;

  /**
   * Construct a new {@link CellBrowser}.
   * 
   * @param <T>
   *          the type of data in the root node
   * @param viewModel
   *          the {@link TreeViewModel} that backs the tree
   * @param rootValue
   *          the hidden root value of the tree
   */
  public <T> DragAndDropCellBrowser(TreeViewModel viewModel, T rootValue) {
    this(viewModel, rootValue, getDefaultResources());
  }

  public <T> DragAndDropCellBrowser(TreeViewModel viewModel, T rootValue,
      Resources resources) {
    super(viewModel, rootValue, resources);
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
   * Add a handler object that will manage the {@link BeforeDragStartEvent}
   * event. this kind of event is fired before the initialization of the drag
   * operation.
   */
  public HandlerRegistration addBeforeDragHandler(
      BeforeDragStartEventHandler handler) {
    return addDragAndDropHandler(handler, BeforeDragStartEvent.TYPE);
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

  protected final <H extends EventHandler> HandlerRegistration addDragAndDropHandler(
      H handler, Type<H> type) {
    return ensureDragAndDropHandlers().addHandler(type, handler);
  }

  /**
   * Create a {@link HasData} that will display items. The {@link HasData} must
   * extend {@link Widget}.
   * 
   * @param <C>
   *          the item type in the list view
   * @param nodeInfo
   *          the node info with child data
   * @param level
   *          the level of the list
   * @return the {@link HasData}
   */
  @Override
  protected <C> BrowserCellList<C> createDisplay(NodeInfo<C> nodeInfo, int level) {
    DragAndDropBrowserCellList<C> display = new DragAndDropBrowserCellList<C>(
        nodeInfo.getCell(), level, nodeInfo.getProvidesKey());

    display.setValueUpdater(nodeInfo.getValueUpdater());

    // Set the keyboard selection policy, but never disable it.
    KeyboardSelectionPolicy keyboardPolicy = getKeyboardSelectionPolicyForLists();
    display.setKeyboardSelectionPolicy(keyboardPolicy);

    /*
     * Drag and drop stuff
     */
    if (nodeInfo instanceof DragAndDropNodeInfo<?>) {
      DragAndDropNodeInfo<C> dndNodeInfo = (DragAndDropNodeInfo<C>) nodeInfo;
      display.setCellDragAndDropBehaviour(dndNodeInfo
          .getCellDragAndDropBehaviour());
      display.setDraggableOptions(dndNodeInfo.getDraggableOptions());
      display.setDroppableOptions(dndNodeInfo.getDroppableOptions());
    }
    return display;
  }

  protected EventBus ensureDragAndDropHandlers() {

    return dragAndDropHandlerManager == null ? dragAndDropHandlerManager = new SimpleEventBus()
        : dragAndDropHandlerManager;
  }

}

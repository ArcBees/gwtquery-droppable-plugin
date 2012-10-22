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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.query.client.Function;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.view.client.ProvidesKey;

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

import java.util.ArrayList;
import java.util.List;


public class DragAndDropCellTable<T> extends CellTable<T> {

  private final List<Column<T, ?>> columns = new ArrayList<Column<T, ?>>();
  
  /**
   * Resources that match the GWT standard style theme.
   */
  public interface BasicResources extends Resources {
    /**
     * The styles used in this widget.
     */
    @Source("gwtquery/plugins/droppable/client/gwt/DragAndDropCellTableBasic.css")
    Style cellTableStyle();
  }
  
  public interface Resources extends com.google.gwt.user.cellview.client.CellTable.Resources{
    Resources INSTANCE = GWT.create(Resources.class);
    
    /**
     * The styles used in this widget.
     */
    @Source("gwtquery/plugins/droppable/client/gwt/DragAndDropCellTable.css")
    Style cellTableStyle();
  }

  private EventBus dragAndDropHandlerManager;

  /**
   * Constructs a table with a default page size of 15.
   */
  public DragAndDropCellTable() {
    super();
  }

  /**
   * Constructs a table with the given page size.
   * 
   * @param pageSize
   *          the page size
   */
  public DragAndDropCellTable(final int pageSize) {
    super(pageSize,Resources.INSTANCE);
  }

  /**
   * Constructs a table with the given page size and the given
   * {@link ProvidesKey key provider}.
   * 
   * @param pageSize
   *          the page size
   * @param keyProvider
   *          an instance of ProvidesKey<T>, or null if the record object should
   *          act as its own key
   */
  public DragAndDropCellTable(final int pageSize, ProvidesKey<T> keyProvider) {
    super(pageSize, Resources.INSTANCE,keyProvider);
  }

  /**
   * Constructs a table with the given page size with the specified
   * {@link Resources}.
   * 
   * @param pageSize
   *          the page size
   * @param resources
   *          the resources to use for this widget
   */
  public DragAndDropCellTable(final int pageSize, Resources resources) {
    super(pageSize, resources);
  }

  /**
   * Constructs a table with the given page size, the specified
   * {@link Resources}, and the given key provider.
   * 
   * @param pageSize
   *          the page size
   * @param resources
   *          the resources to use for this widget
   * @param keyProvider
   *          an instance of ProvidesKey<T>, or null if the record object should
   *          act as its own key
   */
  public DragAndDropCellTable(final int pageSize, Resources resources,
      ProvidesKey<T> keyProvider) {
    super(pageSize, resources, keyProvider);

  }

  /**
   * Constructs a table with a default page size of 15, and the given
   * {@link ProvidesKey key provider}.
   * 
   * @param keyProvider
   *          an instance of ProvidesKey<T>, or null if the record object should
   *          act as its own key
   */
  public DragAndDropCellTable(ProvidesKey<T> keyProvider) {
    super(15,Resources.INSTANCE,keyProvider);
  }

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


  public void addColumn(Column<T, ?> col, Header<?> header, Header<?> footer) {
    super.addColumn(col, header, footer);
    columns.add(col);
  }

  public HandlerRegistration addDeactivateDroppableHandler(
      DeactivateDroppableEventHandler handler) {
    return addDragAndDropHandler(handler, DeactivateDroppableEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DragEvent} event. this
   * kind of event is fired during the move of the widget.
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

  public HandlerRegistration addDropHandler(DropEventHandler handler) {
    return addDragAndDropHandler(handler, DropEvent.TYPE);
  }

  public HandlerRegistration addOutDroppableHandler(
      OutDroppableEventHandler handler) {
    return addDragAndDropHandler(handler, OutDroppableEvent.TYPE);
  }

  public HandlerRegistration addOverDroppableHandler(
      OverDroppableEventHandler handler) {
    return addDragAndDropHandler(handler, OverDroppableEvent.TYPE);
  }

  @Override
  public void removeColumn(int index) {
    super.removeColumn(index);
    columns.remove(index);
  }

  protected final <H extends EventHandler> HandlerRegistration addDragAndDropHandler(
      H handler, Type<H> type) {
    return ensureDrangAndDropHandlers().addHandler(type, handler);
  }

  protected EventBus ensureDrangAndDropHandlers() {

    return dragAndDropHandlerManager == null ? dragAndDropHandlerManager = new SimpleEventBus()
        : dragAndDropHandlerManager;
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

  protected void addDragAndDropBehaviour(List<T> values, int start) {

    int end = start + values.size();

    for (int rowIndex = start; rowIndex < end; rowIndex++) {

      T value = values.get(rowIndex - start);

      for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
        Column<T, ?> column = columns.get(columnIndex);

        if (!(column instanceof DragAndDropColumn<?, ?>)) {
          continue;
        }

        final DragAndDropColumn<T, ?> dndColumn = (DragAndDropColumn<T, ?>) column;

        Element newCell = getCellWrapperDiv(rowIndex, columnIndex);

        DragAndDropCellWidgetUtils.get().maybeMakeDraggableOrDroppable(newCell,
            value, dndColumn.getCellDragAndDropBehaviour(),
            dndColumn.getDraggableOptions(), dndColumn.getDroppableOptions(),
            ensureDrangAndDropHandlers());

      }
    }

  }

  @Override
  protected void replaceChildren(List<T> values, int start, SafeHtml html) {
    // clean cell has being replaced
    int end = start + values.size();
    for (int rowIndex = start; rowIndex < end; rowIndex++) {
      for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
        Element oldCell = getCellWrapperDiv(rowIndex, columnIndex);
        DragAndDropCellWidgetUtils.get().cleanCell(oldCell);
      }

    }

    // lets the super class replace all child
    super.replaceChildren(values, start, html);

    // make the new cell draggable or droppable
    addDragAndDropBehaviour(values, start);
  }

  protected void cleanAllCells() {
    // select all first div inside each tr element and clean it
    $("td > div", getChildContainer()).each(new Function() {
      @Override
      public void f(Element div) {
        DragAndDropCellWidgetUtils.get().cleanCell(div);
      }
    });

  }

  private Element getCellWrapperDiv(int rowIndex, int columnIndex) {
    TableSectionElement tbody = getChildContainer().cast();
    int rowsNbr = tbody.getRows().getLength();
    if (rowIndex < rowsNbr) {
      TableRowElement row = tbody.getRows().getItem(rowIndex);
      int columnNbr = row.getCells().getLength();
      if (columnIndex < columnNbr) {
        return row.getCells().getItem(columnIndex).getFirstChildElement();
      }
    }
    return null;
  }

}

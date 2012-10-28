package gwtquery.plugins.droppable.client.gwt;

import static com.google.gwt.query.client.GQuery.$;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent.DragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent.ActivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent.DeactivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.cellview.client.Column;

/**
 * Class used internally by {@link DragAndDropCellTable} and
 * {@link DragAndDropDataGrid}
 * 
 * @author jDramaix
 * 
 */
class DragAndDropAbstractCellTableDelegate<T> {

	private final List<Column<T, ?>> columns = new ArrayList<Column<T, ?>>();
	private EventBus dragAndDropHandlerManager;

	DragAndDropAbstractCellTableDelegate() {
	}

	 HandlerRegistration addActivateDroppableHandler(
			ActivateDroppableEventHandler handler) {
		return addDragAndDropHandler(handler, ActivateDroppableEvent.TYPE);
	}

	/**
	 * Add a handler object that will manage the {@link BeforeDragStartEvent}
	 * event. this kind of event is fired before the initialization of the drag
	 * operation.
	 */
	 HandlerRegistration addBeforeDragHandler(
			BeforeDragStartEventHandler handler) {
		return addDragAndDropHandler(handler, BeforeDragStartEvent.TYPE);
	}


	 void insertColumn(int beforeIndex, Column<T, ?> col) {
		columns.add(beforeIndex, col);
	}

	 HandlerRegistration addDeactivateDroppableHandler(
			DeactivateDroppableEventHandler handler) {
		return addDragAndDropHandler(handler, DeactivateDroppableEvent.TYPE);
	}

	/**
	 * Add a handler object that will manage the {@link DragEvent} event. this
	 * kind of event is fired during the move of the widget.
	 */
	 HandlerRegistration addDragHandler(DragEventHandler handler) {
		return addDragAndDropHandler(handler, DragEvent.TYPE);
	}

	/**
	 * Add a handler object that will manage the {@link DragStartEvent} event.
	 * This kind of event is fired when the drag operation starts.
	 */
	 HandlerRegistration addDragStartHandler(DragStartEventHandler handler) {
		return addDragAndDropHandler(handler, DragStartEvent.TYPE);
	}

	/**
	 * Add a handler object that will manage the {@link DragStopEvent} event.
	 * This kind of event is fired when the drag operation stops.
	 */
	 HandlerRegistration addDragStopHandler(DragStopEventHandler handler) {
		return addDragAndDropHandler(handler, DragStopEvent.TYPE);
	}

	 HandlerRegistration addDropHandler(DropEventHandler handler) {
		return addDragAndDropHandler(handler, DropEvent.TYPE);
	}

	 HandlerRegistration addOutDroppableHandler(
			OutDroppableEventHandler handler) {
		return addDragAndDropHandler(handler, OutDroppableEvent.TYPE);
	}

	 HandlerRegistration addOverDroppableHandler(
			OverDroppableEventHandler handler) {
		return addDragAndDropHandler(handler, OverDroppableEvent.TYPE);
	}

	 void removeColumn(int index) {
		columns.remove(index);
	}

	 final <H extends EventHandler> HandlerRegistration addDragAndDropHandler(
			H handler, Type<H> type) {
		return ensureDrangAndDropHandlers().addHandler(type, handler);
	}

	 EventBus ensureDrangAndDropHandlers() {

		return dragAndDropHandlerManager == null ? dragAndDropHandlerManager = new SimpleEventBus()
				: dragAndDropHandlerManager;
	}

	 void addDragAndDropBehaviour(List<T> values, int start, Element childContainer) {

		int end = start + values.size();

		for (int rowIndex = start; rowIndex < end; rowIndex++) {

			T value = values.get(rowIndex - start);

			for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
				Column<T, ?> column = columns.get(columnIndex);

				if (!(column instanceof DragAndDropColumn<?, ?>)) {
					continue;
				}

				final DragAndDropColumn<T, ?> dndColumn = (DragAndDropColumn<T, ?>) column;

				Element newCell = getCellWrapperDiv(rowIndex, columnIndex, childContainer);

				DragAndDropCellWidgetUtils.get().maybeMakeDraggableOrDroppable(
						newCell, value,
						dndColumn.getCellDragAndDropBehaviour(),
						dndColumn.getDraggableOptions(),
						dndColumn.getDroppableOptions(),
						ensureDrangAndDropHandlers());

			}
		}

	}

	void cleanCellRange(int start, int end, Element childContainer){
		
		for (int rowIndex = start; rowIndex < end; rowIndex++) {
			for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
				Element oldCell = getCellWrapperDiv(rowIndex, columnIndex, childContainer);
				DragAndDropCellWidgetUtils.get().cleanCell(oldCell);
			}

		}
		
	}

	 void cleanAllCells(Element childContainer) {
		// select all first div inside each tr element and clean it
		$("td > div", childContainer).each(new Function() {
			@Override
			public void f(Element div) {
				DragAndDropCellWidgetUtils.get().cleanCell(div);
			}
		});

	}

	private Element getCellWrapperDiv(int rowIndex, int columnIndex, Element childContainer) {
		TableSectionElement tbody = childContainer.cast();
		int rowsNbr = tbody.getRows().getLength();
		if (rowIndex < rowsNbr) {
			TableRowElement row = tbody.getRows().getItem(rowIndex);
			int columnNbr = row.getCells().getLength();
			if (columnIndex < columnNbr) {
				return row.getCells().getItem(columnIndex)
						.getFirstChildElement();
			}
		}
		return null;
	}

}

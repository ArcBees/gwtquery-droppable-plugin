package gwtquery.plugins.droppable.client.gwt;

import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent.DragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent.ActivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent.DeactivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

public class DragAndDropDataGrid<T> extends DataGrid<T> {

	public interface Resources extends
			com.google.gwt.user.cellview.client.DataGrid.Resources {
		Resources INSTANCE = GWT.create(Resources.class);

		/**
		 * The styles used in this widget.
		 */
		@Source("gwtquery/plugins/droppable/client/gwt/DragAndDropDataGrid.css")
		Style dataGridStyle();
	}

	private static Resources DEFAULT_RESOURCES;
	private static final int DEFAULT_PAGESIZE = 50;

	private static Resources getDefaultResources() {
		if (DEFAULT_RESOURCES == null) {
			DEFAULT_RESOURCES = GWT.create(Resources.class);
		}
		return DEFAULT_RESOURCES;
	}

	private DragAndDropAbstractCellTableDelegate<T> delegate;

	/**
	 * Constructs a table with a default page size of 50.
	 */
	public DragAndDropDataGrid() {
		this(DEFAULT_PAGESIZE);
	}

	/**
	 * Constructs a table with the given page size.
	 * 
	 * @param pageSize
	 *            the page size
	 */
	public DragAndDropDataGrid(final int pageSize) {
		super(pageSize, getDefaultResources());
		delegate = new DragAndDropAbstractCellTableDelegate<T>();
	}

	/**
	 * Constructs a table with the given page size and the given
	 * {@link ProvidesKey key provider}.
	 * 
	 * @param pageSize
	 *            the page size
	 * @param keyProvider
	 *            an instance of ProvidesKey<T>, or null if the record object
	 *            should act as its own key
	 */
	public DragAndDropDataGrid(int pageSize, ProvidesKey<T> keyProvider) {
		super(pageSize, getDefaultResources(), keyProvider);
		delegate = new DragAndDropAbstractCellTableDelegate<T>();
	}

	/**
	 * Constructs a table with the given page size with the specified
	 * {@link Resources}.
	 * 
	 * @param pageSize
	 *            the page size
	 * @param resources
	 *            the resources to use for this widget
	 */
	public DragAndDropDataGrid(int pageSize, Resources resources) {
		super(pageSize, resources);
		delegate = new DragAndDropAbstractCellTableDelegate<T>();
	}

	/**
	 * Constructs a table with the given page size, the specified
	 * {@link Resources}, and the given key provider.
	 * 
	 * @param pageSize
	 *            the page size
	 * @param resources
	 *            the resources to use for this widget
	 * @param keyProvider
	 *            an instance of ProvidesKey<T>, or null if the record object
	 *            should act as its own key
	 */
	public DragAndDropDataGrid(int pageSize, Resources resources,
			ProvidesKey<T> keyProvider) {
		super(pageSize, resources, keyProvider);
		delegate = new DragAndDropAbstractCellTableDelegate<T>();
	}

	/**
	 * Constructs a table with the given page size, the specified
	 * {@link Resources}, and the given key provider.
	 * 
	 * @param pageSize
	 *            the page size
	 * @param resources
	 *            the resources to use for this widget
	 * @param keyProvider
	 *            an instance of ProvidesKey<T>, or null if the record object
	 *            should act as its own key
	 * @param loadingIndicator
	 *            the widget to use as a loading indicator, or null to disable
	 */
	public DragAndDropDataGrid(int pageSize, Resources resources,
			ProvidesKey<T> keyProvider, Widget loadingIndicator) {
		super(pageSize, resources, keyProvider, loadingIndicator);
		delegate = new DragAndDropAbstractCellTableDelegate<T>();
	}

	public HandlerRegistration addActivateDroppableHandler(
			ActivateDroppableEventHandler handler) {
		return delegate.addActivateDroppableHandler(handler);
	}

	/**
	 * Add a handler object that will manage the {@link BeforeDragStartEvent}
	 * event. this kind of event is fired before the initialization of the drag
	 * operation.
	 */
	public HandlerRegistration addBeforeDragHandler(
			BeforeDragStartEventHandler handler) {
		return delegate.addBeforeDragHandler(handler);
	}

	@Override
	public void insertColumn(int beforeIndex, Column<T, ?> col,
			Header<?> header, Header<?> footer) {
		super.insertColumn(beforeIndex, col, header, footer);
		delegate.insertColumn(beforeIndex, col);
	}

	public HandlerRegistration addDeactivateDroppableHandler(
			DeactivateDroppableEventHandler handler) {
		return delegate.addDeactivateDroppableHandler(handler);
	}

	/**
	 * Add a handler object that will manage the {@link DragEvent} event. this
	 * kind of event is fired during the move of the widget.
	 */
	public HandlerRegistration addDragHandler(DragEventHandler handler) {
		return delegate.addDragHandler(handler);
	}

	/**
	 * Add a handler object that will manage the {@link DragStartEvent} event.
	 * This kind of event is fired when the drag operation starts.
	 */
	public HandlerRegistration addDragStartHandler(DragStartEventHandler handler) {
		return delegate.addDragStartHandler(handler);
	}

	/**
	 * Add a handler object that will manage the {@link DragStopEvent} event.
	 * This kind of event is fired when the drag operation stops.
	 */
	public HandlerRegistration addDragStopHandler(DragStopEventHandler handler) {
		return delegate.addDragStopHandler(handler);
	}

	public HandlerRegistration addDropHandler(DropEventHandler handler) {
		return delegate.addDropHandler(handler);
	}

	public HandlerRegistration addOutDroppableHandler(
			OutDroppableEventHandler handler) {
		return delegate.addOutDroppableHandler(handler);
	}

	public HandlerRegistration addOverDroppableHandler(
			OverDroppableEventHandler handler) {
		return delegate.addOverDroppableHandler(handler);
	}

	@Override
	public void removeColumn(int index) {
		super.removeColumn(index);
		delegate.removeColumn(index);
	}

	@Override
	protected void onUnload() {

		delegate.cleanAllCells(getChildContainer());

		super.onUnload();
	}

	@Override
	protected void replaceAllChildren(List<T> values, SafeHtml html) {
		// first clean old cell before remove it
		delegate.cleanAllCells(getChildContainer());

		// lets the super class replace all child
		super.replaceAllChildren(values, html);

		// make the new cell draggable or droppable
		delegate.addDragAndDropBehaviour(values, 0, getChildContainer());

	}

	@Override
	protected void replaceChildren(List<T> values, int start, SafeHtml html) {
		// clean cell has being replaced
		int end = start + values.size();
		delegate.cleanCellRange(start, end, getChildContainer());

		// lets the super class replace all child
		super.replaceChildren(values, start, html);

		// make the new cell draggable or droppable
		delegate.addDragAndDropBehaviour(values, start, getChildContainer());
	}

}

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
import static gwtquery.plugins.droppable.client.Droppable.Droppable;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.query.client.plugins.events.EventsListener;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.HasAllDropHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent.ActivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent.DeactivateDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;

/**
 * Wrapper widget that wrap an GWT widget and make it a drop target.
 * 
 * This class can be used as a wrapper or subclassed.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 * @param <T>
 */
public class DroppableWidget<T extends Widget> extends Composite implements
    HasAllDropHandler {

  private final static String DROPPABLE_WIDGET_KEY = "__droppableWidget";

  /**
   * This method return the widget associated to a droppable DOM element if it
   * exist. It returns null otherwise.
   * 
   * @param e
   *          a droppable DOM element
   * @return
   */
  public static DroppableWidget<?> get(Element e) {
    return $(e).data(DROPPABLE_WIDGET_KEY, DroppableWidget.class);
  }

  private EventBus dropHandlerManager;
  private DroppableOptions options;

  /**
   * Constructor
   * 
   * @param w
   *          the widget that you want to make it a drop target.
   */
  public DroppableWidget(T w) {
    this(w, new DroppableOptions(), DraggableOptions.DEFAULT_SCOPE);
  }

  /**
   * Constructor
   * 
   * @param w
   *          the widget that you want to make it a drop target.
   * @param options
   *          options to use during the drop operation
   */
  public DroppableWidget(T w, DroppableOptions options) {
    this(w, options, DraggableOptions.DEFAULT_SCOPE);
  }

  /**
   * Constructor
   * 
   * @param w
   *          the widget that you want to make it a drop target.
   * @param options
   *          options to use during the drop operation
   * @param scope
   *          Used to group sets of draggable and droppable Widget, in addition
   *          to droppable's accept option. A draggable with the same scope
   *          value as a droppable will be accepted.
   */
  public DroppableWidget(T w, DroppableOptions options, String scope) {
    initWidget(w);
    this.options = options;
    this.options.setScope(scope);
  }

  /**
   * Add possibility to extend this widget. As {@link DroppableWidget} is a
   * {@link Composite}, don't forget to call the {@link #initWidget(Widget)}
   * method
   */
  protected DroppableWidget() {
    options = new DroppableOptions();
  }

  /**
   * Add a handler object that will manage the {@link ActivateDroppableEvent}
   * event. This kind of event is fired when an acceptable draggable start to
   * drag.
   * 
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public HandlerRegistration addActivateDroppableHandler(
      ActivateDroppableEventHandler handler) {
    return addDropHandler(handler, ActivateDroppableEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DeactivateDroppableEvent}
   * event. This kind of event is fired when an acceptable draggable finishes to
   * drag.
   * 
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public HandlerRegistration addDeactivateDroppableHandler(
      DeactivateDroppableEventHandler handler) {
    return addDropHandler(handler, DeactivateDroppableEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link DropEvent} event. This
   * kind of event is fired when an acceptable draggable is dropped in the
   * droppable
   * 
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public HandlerRegistration addDropHandler(DropEventHandler handler) {
    return addDropHandler(handler, DropEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link OutDroppableEvent} event.
   * This kind of event is fired when an acceptable draggable is being dragged
   * out of the droppable.
   * 
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public HandlerRegistration addOutDroppableHandler(
      OutDroppableEventHandler handler) {
    return addDropHandler(handler, OutDroppableEvent.TYPE);
  }

  /**
   * Add a handler object that will manage the {@link OutDroppableEvent} event.
   * This kind of event is fired when a acceptable draggable is being dragged
   * over the droppable.
   * 
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public HandlerRegistration addOverDroppableHandler(
      OverDroppableEventHandler handler) {
    return addDropHandler(handler, OverDroppableEvent.TYPE);
  }

  /**
   * 
   * @return the {@link AcceptFunction}
   */
  public AcceptFunction getAccept() {
    return options.getAccept();
  }

  /**
   * 
   * @return the css class added to the droppable when is activated
   */
  public String getActiveClass() {
    return options.getActiveClass();
  }
  
  /**
   * Return the drag and drop scope. A draggable widget with the same scope than
   * a droppable widget will be accepted by this droppable.
   * 
   * @return the scope
   */
  public String getDragAndDropScope() {
    return options.getScope();
  }

  /**
   * @return the css class added to an acceptable draggable when it is
   *         being dragged over a droppable
   * 
   */
  public String getDraggableHoverClass() {
    return options.getDraggableHoverClass();
  }

  /**
   * 
   * @return the css class added to the droppable when is hovered
   */
  public String getDroppableHoverClass() {
    return options.getDroppableHoverClass();
  }

  /**
   * 
   * @return the {@link DroppableOptions} currently used.
   */
  public DroppableOptions getOptions() {
    return options;
  }

  /**
   * Get the wrapped original widget
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public T getOriginalWidget() {
    return (T) getWidget();
  }

  /**
   * 
   * @return the {@link DroppableTolerance}
   */
  public DroppableTolerance getTolerance() {
    return options.getTolerance();
  }

  /**
   * 
   * @return if the drop is disabled or not.
   */
  public boolean isDisabled() {
    return options.isDisabled();
  }

  /**
   * Specify if the droppable is greedy or not. A greedy droppable will prevent
   * events propagation on drappable parents of this droppable
   * 
   * @return true if the droppable is greedy. False otherwise
   */
  public boolean isGreedy() {
    return options.isGreedy();
  }

  /**
   * set the {@link AcceptFunction}. If null is given, the droppable will accept
   * all draggables.
   * 
   * @param acceptFunction
   */
  public void setAccept(AcceptFunction acceptFunction) {
    if (acceptFunction != null) {
      options.setAccept(acceptFunction);
    } else {
      options.setAccept(DroppableOptions.ACCEPT_ALL);
    }
  }

  /**
   * Set the css selector used to define elements that will be accepted by this
   * droppable
   * 
   * @param selector
   */
  public void setAccept(String selector) {
    options.setAccept(selector);
  }

  /**
   * Set the css class that will be add to the droppable when it is activated
   * 
   * @param activeClass
   */
  public void setActiveClass(String activeClass) {
    options.setActiveClass(activeClass);
  }

  /**
   * Disable (true) or enable (false) the drop.
   * 
   * @param disabled
   */
  public void setDisabled(boolean disabled) {
    options.setDisabled(disabled);
  }

  /**
   * Used to group sets of draggable and droppable widget, in addition to
   * droppable's {@link AcceptFunction}. A DraggableWidget with the same scope
   * value than a DroppableWidget will be accepted by this last.
   * 
   * @param scope
   */
  public void setDragAndDropScope(String scope) {
    $(getElement()).as(Droppable).changeScope(scope);
    options.setScope(scope);
  }
  
  /**
   * Set the css class added to an acceptable draggable when it is
   *         being dragged over the droppable
   *
   * @param draggableHoverClass
   */
  public void setDraggableHoverClass(String draggableHoverClass) {
    options.setDraggableHoverClass(draggableHoverClass);
  }

  /**
   * Css class added when a acceptable draggable is being dragged over this
   * droppable
   * 
   * @param hoverClass
   *          css class
   */
  public void setDroppableHoverClass(String hoverClass) {
    options.setDroppableHoverClass(hoverClass);
  }

  /**
   * When set to true it prevents events propagation on parents droppable of
   * this droppable
   * 
   * @param greedy
   */
  public void setGreedy(boolean greedy) {
    options.setGreedy(greedy);
  }

  /**
   * set the {@link DroppableTolerance}
   * 
   * @param tolerance
   */
  public void setTolerance(DroppableTolerance tolerance) {
    options.setTolerance(tolerance);
  }

  protected final <H extends EventHandler> HandlerRegistration addDropHandler(
      H handler, Type<H> type) {
    return ensureDropHandlers().addHandler(type, handler);
  }

  protected EventBus ensureDropHandlers() {
    return dropHandlerManager == null ? dropHandlerManager = new SimpleEventBus()
        : dropHandlerManager;
  }

  protected EventBus getDropHandlerManager() {
    return dropHandlerManager;
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    // force using of EventListener from GQuery !
    EventsListener gQueryEventListener = EventsListener
        .getInstance(getElement());
    if (DOM.getEventListener(getElement()) != gQueryEventListener) {
      DOM.setEventListener(getElement(), gQueryEventListener);
    }
    $(getElement()).as(Droppable).droppable(options, ensureDropHandlers())
        .data(DROPPABLE_WIDGET_KEY, this);
  }

  @Override
  protected void onUnload() {
    super.onUnload();
    $(getElement()).as(Droppable).destroy().removeData(DROPPABLE_WIDGET_KEY);
  }

}

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
package gwtquery.plugins.droppable.client;

import static com.google.gwt.query.client.GQuery.$;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;

/**
 * This class is used to configure the drop operation.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */
public class DroppableOptions {

  /**
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public static interface AcceptFunction {

    /**
     * This method returns true if the draggable can be dropped on the droppable
     * 
     * @param droppable
     * @param draggable
     * @return
     */
    public boolean acceptDrop(DragAndDropContext context);

  }

  /**
   * Object use as callback function
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public static interface DroppableFunction {
    public void f(DragAndDropContext context);
  }

  /**
   * Specifies which mode to use for testing whether a draggable is 'over' a
   * droppable.
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   */
  public static enum DroppableTolerance {
    /**
     * FIT: the draggable has to overlap the droppable entirely
     */
    FIT,

    /**
     * INTERSECT: draggable has to overlap the droppable at least 50%
     */
    INTERSECT,

    /**
     * POINTER: mouse pointer has to overlap the droppable
     */
    POINTER,

    /**
     * TOUCH: draggable has to overlap the droppable any amount
     */
    TOUCH;
  }

  /**
   * Implementation of {@link AcceptFunction} that accepts all draggable
   */
  public static AcceptFunction ACCEPT_ALL = new AcceptFunction() {
    public boolean acceptDrop(DragAndDropContext context) {
      return true;
    }
  };

  private AcceptFunction accept;
  private String activeClass;
  private boolean disabled;
  private String draggableHoverClass;
  private String droppableHoverClass;
  private boolean greedy;
  private DroppableFunction onActivate;
  private DroppableFunction onDeactivate;
  private DroppableFunction onDrop;
  private DroppableFunction onOut;
  private DroppableFunction onOver;
  private String scope;
  private DroppableTolerance tolerance;

  /**
   * Constructor
   */
  public DroppableOptions() {
    initDefault();
  }

  /**
   * @return the {@link AcceptFunction}
   */
  public AcceptFunction getAccept() {
    return accept;
  }

  /**
   * @return the class added to the droppable when it is active (when an
   *         acceptable draggable is dragging).
   * 
   */
  public String getActiveClass() {
    return activeClass;
  }

  /**
   * @return the css class added to an acceptable draggable when it is being
   *         dragged over a droppable
   * 
   */
  public String getDraggableHoverClass() {
    return draggableHoverClass;
  }

  /**
   * @return the class added to the droppable when an acceptable draggable is
   *         being dragged over it
   * 
   */
  public String getDroppableHoverClass() {
    return droppableHoverClass;
  }

  /**
   * @return the {@link DroppableFunction} called when a droppable is activated
   * 
   */
  public DroppableFunction getOnActivate() {
    return onActivate;
  }

  /**
   * @return the {@link DroppableFunction} called when a droppable is
   *         deactivated
   * 
   */
  public DroppableFunction getOnDeactivate() {
    return onDeactivate;
  }

  /**
   * @return the {@link DroppableFunction} called when an acceptable draggable
   *         is dropped on a droppable
   * 
   */
  public DroppableFunction getOnDrop() {
    return onDrop;
  }

  /**
   * @return the {@link DroppableFunction} called when an acceptable draggable
   *         is being dragged out of a droppable
   * 
   */
  public DroppableFunction getOnOut() {
    return onOut;
  }

  /**
   * @return the {@link DroppableFunction} called when an acceptable draggable
   *         is being dragged over a droppable
   * 
   */
  public DroppableFunction getOnOver() {
    return onOver;
  }

  /**
   * @return the scope of the droppable. The scope is used to group sets of
   *         draggable and droppable items, in addition to droppable's accept
   *         option. A draggable with the same scope value as a droppable will
   *         be accepted by the droppable.
   * 
   */
  public String getScope() {
    return scope;
  }

  /**
   * @return the {@link DroppableTolerance}
   */
  public DroppableTolerance getTolerance() {
    return tolerance;
  }

  /**
   * Specify if the drop is disabled or not
   * 
   * @return true if the drop is disabled, false otherwise.
   */
  public boolean isDisabled() {
    return disabled;
  }

  /**
   * Specify if the droppable is greedy or not. A greedy droppable will prevent
   * events propagation on drappable parents of this droppable
   * 
   * @return true if the droppable is greedy. False otherwise
   */
  public boolean isGreedy() {
    return greedy;
  }

  /**
   * @return Set the {@link AcceptFunction}. If null is given, the droppable
   *         will accept all draggables.
   */
  public void setAccept(AcceptFunction acceptFunction) {
    this.accept = acceptFunction;
  }

  /**
   * Set the css selector used to define elements that will be accepted by this
   * droppable
   * 
   * @param selector
   */
  public void setAccept(final String selector) {
    this.accept = new AcceptFunction() {
      public boolean acceptDrop(DragAndDropContext context) {
        return $(context.getDraggable()).is(selector);
      }
    };
  }

  /**
   * Set the css class that will be add to the droppable when it is activated
   * 
   * @param activeClass
   */
  public void setActiveClass(String activeClass) {
    this.activeClass = activeClass;
  }

  /**
   * Disable (true) or enable (false) the drop.
   * 
   * @param disabled
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  /**
   * Set the css class that will be added to an acceptable draggable when it is
   * being dragged over the droppable
   * 
   * @param draggableHoverClass
   */
  public void setDraggableHoverClass(String draggableHoverClass) {
    this.draggableHoverClass = draggableHoverClass;
  }

  /**
   * Set the css class that will be added to a droppable when an acceptable
   * draggable is being dragged over it
   * 
   * @param draggableHoverClass
   */
  public void setDroppableHoverClass(String hoverClass) {
    this.droppableHoverClass = hoverClass;
  }

  /**
   * When set to true it prevents events propagation on parents droppable of
   * this droppable
   * 
   * @param greedy
   */
  public void setGreedy(boolean greedy) {
    this.greedy = greedy;
  }

  /**
   * Set the {@link DroppableFunction} called when a droppable is activated
   * 
   * @param onActivate
   */
  public void setOnActivate(DroppableFunction onActivate) {
    this.onActivate = onActivate;
  }

  /**
   * Set the {@link DroppableFunction} called when a droppable is deactivated
   * 
   * @param onActivate
   */
  public void setOnDeactivate(DroppableFunction onDeactivate) {
    this.onDeactivate = onDeactivate;
  }

  /**
   * Set the {@link DroppableFunction} called when an acceptable draggable is
   * dropped a droppable
   * 
   * @param onActivate
   */
  public void setOnDrop(DroppableFunction onDrop) {
    this.onDrop = onDrop;
  }

  /**
   * Set the {@link DroppableFunction} called when an acceptable draggable is
   * being dragged out of a droppable
   * 
   * @param onActivate
   */
  public void setOnOut(DroppableFunction onOut) {
    this.onOut = onOut;
  }

  /**
   * Set the {@link DroppableFunction} called when an acceptable draggable is
   * being dragged over a droppable
   * 
   * @param onActivate
   */
  public void setOnOver(DroppableFunction onOver) {
    this.onOver = onOver;
  }

  /**
   * Used to group sets of draggable and droppable widget, in addition to
   * droppable's {@link AcceptFunction}. A DraggableWidget with the same scope
   * value than a DroppableWidget will be accepted by this last.
   * 
   * @param scope
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  public void setTolerance(DroppableTolerance tolerance) {
    this.tolerance = tolerance;
  }

  protected void initDefault() {
    setAccept(ACCEPT_ALL);
    activeClass = null;
    greedy = false;
    droppableHoverClass = null;
    draggableHoverClass = null;
    scope = "default";
    tolerance = DroppableTolerance.INTERSECT;

  }

}

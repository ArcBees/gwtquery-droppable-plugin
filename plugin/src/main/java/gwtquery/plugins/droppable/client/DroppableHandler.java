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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.GQuery.Offset;
import com.google.gwt.query.client.plugins.UiPlugin.Dimension;
import com.google.gwt.query.client.plugins.events.GqEvent;

import static gwtquery.plugins.droppable.client.Droppable.DROPPABLE_HANDLER_KEY;

import gwtquery.plugins.draggable.client.DragAndDropManager;
import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.droppable.client.Droppable.CssClassNames;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableFunction;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.events.AbstractDroppableEvent;
import gwtquery.plugins.droppable.client.events.ActivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.DeactivateDroppableEvent;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;

/**
 * Class implementing the core of the droppable plugin
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class DroppableHandler {

  private static enum PositionStatus {
    IS_OUT, IS_OVER;
  }

  public static DroppableHandler getInstance(Element droppable) {
    return $(droppable).data(DROPPABLE_HANDLER_KEY, DroppableHandler.class);
  }

  private Dimension droppableDimension;
  private Offset droppableOffset;
  private HasHandlers eventBus;
  private boolean greedyChild = false;
  private boolean isOut = true;
  private boolean isOver = false;
  private DroppableOptions options;

  private boolean visible = false;

  public DroppableHandler(DroppableOptions options, HasHandlers eventBus) {
    this.options = options;
    this.eventBus = eventBus;

  }

  public void activate(Element droppable, GqEvent e) {
    if (options.getActiveClass() != null) {
      droppable.addClassName(options.getActiveClass());
    }
    Element draggable = DragAndDropManager.getInstance().getCurrentDraggable();
    if (draggable != null) {
      if (options.getDraggableHoverClass() != null) {
        $(draggable).data(options.getDraggableHoverClass(), new Integer(0));
      }

      DragAndDropContext ctx = new DragAndDropContext(draggable, droppable);
      trigger(new ActivateDroppableEvent(ctx), options.getOnActivate(), ctx);
    }
  }

  public void deactivate(Element droppable, GqEvent e) {
    if (options.getActiveClass() != null) {
      droppable.removeClassName(options.getActiveClass());
    }
    if (options.getDroppableHoverClass() != null) {
      droppable.removeClassName(options.getDroppableHoverClass());
    }
    Element draggable = DragAndDropManager.getInstance().getCurrentDraggable();
    if (draggable != null) {
      if (options.getDraggableHoverClass() != null) {
        DraggableHandler dragHandler = DraggableHandler.getInstance(draggable);
        dragHandler.getHelper().removeClass(options.getDraggableHoverClass());
        $(draggable).removeData(options.getDraggableHoverClass());
      }

      DragAndDropContext ctx = new DragAndDropContext(draggable, droppable);
      trigger(new DeactivateDroppableEvent(ctx), options.getOnDeactivate(), ctx);
    }

  }

  public void drag(Element droppable, Element draggable, GqEvent e) {
    if (options.isDisabled() || greedyChild || !visible) {
      return;
    }

    boolean isIntersect = intersect(draggable);
    PositionStatus c = null;

    if (!isIntersect && isOver) {
      c = PositionStatus.IS_OUT;
    } else if (isIntersect && !isOver) {
      c = PositionStatus.IS_OVER;
    }
    if (c == null) {
      return;
    }

    DroppableHandler parentDroppableHandler = null;
    GQuery droppableParents = null;
    if (options.isGreedy()) {
      // TODO maybe filter the parent with droppable data instead of test on css
      // class name
      droppableParents = $(droppable).parents(
          "." + CssClassNames.GWTQUERY_DROPPABLE);
      if (droppableParents.length() > 0) {
        parentDroppableHandler = DroppableHandler.getInstance(droppableParents
            .get(0));
        parentDroppableHandler.greedyChild = (c == PositionStatus.IS_OVER);
      }
    }

    if (parentDroppableHandler != null && c == PositionStatus.IS_OVER) {
      parentDroppableHandler.isOver = false;
      parentDroppableHandler.isOut = true;
      parentDroppableHandler.out(droppableParents.get(0), draggable, e);
    }

    if (c == PositionStatus.IS_OUT) {
      isOut = true;
      isOver = false;
      out(droppable, draggable, e);
    } else {
      isOver = true;
      isOut = false;
      over(droppable, draggable, e);
    }

    if (parentDroppableHandler != null && c == PositionStatus.IS_OUT) {
      parentDroppableHandler.isOut = false;
      parentDroppableHandler.isOver = true;
      parentDroppableHandler.over(droppableParents.get(0), draggable, e);
    }

  }

  public boolean drop(Element droppable, Element draggable, GqEvent e,
      boolean alreadyDrop) {
    if (options == null) {
      return false;
    }

    boolean drop = false;

    if (!options.isDisabled() && visible) {
      if (intersect(draggable)
          && !checkChildrenIntersection(droppable, draggable)
          && isDraggableAccepted(droppable, draggable)) {

        final DragAndDropContext ctx = new DragAndDropContext(draggable,
            droppable);

        // we will use a deferredComand to trigger the drop event a the end of
        // the drag and drop operation !!
        // it's to ensure that the rest of the dnd operation will be done
        // without perturbation
        // (e.g. on the drop event we can remove the draggable..)
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

          public void execute() {
            trigger(new DropEvent(ctx), options.getOnDrop(), ctx);

          }
        });
        drop = true;

      }

      if (drop || isDraggableAccepted(droppable, draggable)) {
        isOut = true;
        isOver = false;
        deactivate(droppable, e);
      }
    }
    return drop;
  }

  public Dimension getDroppableDimension() {
    return droppableDimension;
  }

  public Offset getDroppableOffset() {
    return droppableOffset;
  }

  public DroppableOptions getOptions() {
    return options;
  }

  public boolean isOut() {
    return isOut;
  }

  public boolean isOver() {
    return isOver;
  }

  public boolean isVisible() {
    return visible;
  }

  public void out(Element droppable, Element currentDraggable, GqEvent e) {
    if (currentDraggable == null || currentDraggable == droppable) {
      return;
    }

    if (isDraggableAccepted(droppable, currentDraggable)) {
      if (options.getDroppableHoverClass() != null) {
        droppable.removeClassName(options.getDroppableHoverClass());
      }
      if (options.getDraggableHoverClass() != null) {
        Integer counter = $(currentDraggable).data(
            options.getDraggableHoverClass(), Integer.class);
        $(currentDraggable).data(options.getDraggableHoverClass(),
            new Integer(--counter));
        if (counter == 0) {
          DraggableHandler dragHandler = DraggableHandler
              .getInstance(currentDraggable);
          dragHandler.getHelper().removeClass(options.getDraggableHoverClass());
        }
      }

      DragAndDropContext ctx = new DragAndDropContext(currentDraggable,
          droppable);
      trigger(new OutDroppableEvent(ctx), options.getOnOut(), ctx);
    }
  }

  public void over(Element droppable, Element currentDraggable, GqEvent e) {

    if (currentDraggable == null || currentDraggable == droppable) {
      return;
    }

    if (isDraggableAccepted(droppable, currentDraggable)) {
      if (options.getDroppableHoverClass() != null) {
        droppable.addClassName(options.getDroppableHoverClass());
      }
      if (options.getDraggableHoverClass() != null) {
        DraggableHandler dragHandler = DraggableHandler
            .getInstance(currentDraggable);
        dragHandler.getHelper().addClass(options.getDraggableHoverClass());
        Integer counter = $(currentDraggable).data(
            options.getDraggableHoverClass(), Integer.class);
        $(currentDraggable).data(options.getDraggableHoverClass(),
            new Integer(++counter));
      }
      DragAndDropContext ctx = new DragAndDropContext(currentDraggable,
          droppable);
      trigger(new OverDroppableEvent(ctx), options.getOnOver(), ctx);
    }

  }

  public void reset() {
    droppableDimension = null;
    droppableOffset = null;

  }

  public void setDroppableDimension(Dimension droppableDimension) {
    this.droppableDimension = droppableDimension;
  }

  public void setDroppableOffset(Offset offset) {
    this.droppableOffset = offset;

  }

  public void setOptions(DroppableOptions options) {
    this.options = options;
  }

  public void setOut(boolean isOut) {
    this.isOut = isOut;
  }

  public void setOver(boolean isOver) {
    this.isOver = isOver;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;

  }

  private boolean checkChildrenIntersection(Element droppable, Element draggable) {

    for (Element e : $(droppable).find("*").not(".ui-draggable-dragging")
        .elements()) {
      DroppableHandler handler = getInstance(e);
      if (handler == null) {
        continue;
      }
      handler.setDroppableOffset($(e).offset());
      DraggableHandler draggableHandler = DraggableHandler
          .getInstance(draggable);
      DroppableOptions dropOpt = handler.getOptions();
      DraggableOptions dragOpt = draggableHandler.getOptions();
      if (dropOpt.isGreedy() && !dropOpt.isDisabled()
          && dropOpt.getScope().equals(dragOpt.getScope())
          && handler.isDraggableAccepted(droppable, draggable)
          && handler.intersect(draggable)) {
        return true;
      }

    }
    return false;
  }

  private boolean intersect(Element draggable) {
    if (droppableOffset == null || droppableDimension == null) {
      return false;
    }
    DraggableHandler dragHandler = DraggableHandler.getInstance(draggable);

    int draggableLeft = dragHandler.getAbsPosition().left;
    int draggableRight = draggableLeft
        + dragHandler.getHelperDimension().getWidth();
    int draggableTop = dragHandler.getAbsPosition().top;
    int draggableBottom = draggableTop
        + dragHandler.getHelperDimension().getHeight();

    int droppableLeft = droppableOffset.left;
    int droppableRight = droppableLeft + droppableDimension.getWidth();
    int droppableTop = droppableOffset.top;
    int droppableBottom = droppableTop + droppableDimension.getHeight();

    DroppableTolerance tolerance = options.getTolerance();
    // FIT, INTERSECT, POINTER, TOUCH;
    switch (tolerance) {
    case FIT:
      return droppableLeft <= draggableLeft && draggableRight <= droppableRight
          && droppableTop <= draggableTop && draggableBottom <= droppableBottom;
    case INTERSECT:
      float dragHelperHalfWidth = dragHandler.getHelperDimension().getWidth() / 2;
      float dragHelperHalfHeight = dragHandler.getHelperDimension().getHeight() / 2;
      return droppableLeft < draggableLeft + dragHelperHalfWidth
          && droppableRight > draggableLeft + dragHelperHalfWidth
          && droppableTop < draggableTop + dragHelperHalfHeight
          && droppableBottom > draggableTop + dragHelperHalfHeight;
    case POINTER:
      int pointerLeft = draggableLeft + dragHandler.getOffsetClick().left;
      int pointerTop = draggableTop + dragHandler.getOffsetClick().top;
      return pointerLeft > droppableLeft && pointerLeft < droppableRight
          && pointerTop > droppableTop && pointerTop < droppableBottom;

    case TOUCH:
      return ((draggableTop >= droppableTop && draggableTop <= droppableBottom)
          || (draggableBottom >= droppableTop && draggableBottom <= droppableBottom) || (draggableTop < droppableTop && draggableBottom > droppableBottom))
          && ((draggableLeft >= droppableLeft && draggableLeft <= droppableRight)
              || (draggableRight >= droppableLeft && draggableRight <= droppableRight) || (draggableLeft < droppableLeft && draggableRight > droppableRight));

    default:
      break;
    }
    return true;
  }

  private boolean isDraggableAccepted(Element droppable, Element draggable) {
    AcceptFunction accept = options.getAccept();
    return accept != null
        && accept.acceptDrop(new DragAndDropContext(draggable, droppable));
  }

  private void trigger(AbstractDroppableEvent<?> e, DroppableFunction callback,
      DragAndDropContext context) {

    if (eventBus != null && e != null) {
      eventBus.fireEvent(e);
    }
    if (callback != null) {
      callback.f(context);
    }
  }

}

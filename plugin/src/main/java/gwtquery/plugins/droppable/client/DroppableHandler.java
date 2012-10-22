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
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
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

  public void activate(DragAndDropContext ctx, GqEvent e) {
    Element droppable = ctx.getDroppable();

    if (options.getActiveClass() != null) {
      droppable.addClassName(options.getActiveClass());
    }

    for (Element draggable : ctx.getSelectedDraggables()) {

      if (options.getDraggableHoverClass() != null) {
        $(draggable).data(options.getDraggableHoverClass(), new Integer(0));
      }
    }

    trigger(new ActivateDroppableEvent(ctx), options.getOnActivate(), ctx);

  }

  public void deactivate(DragAndDropContext ctx, GqEvent e) {
    Element droppable = ctx.getDroppable();

    if (options.getActiveClass() != null) {
      droppable.removeClassName(options.getActiveClass());
    }
    if (options.getDroppableHoverClass() != null) {
      droppable.removeClassName(options.getDroppableHoverClass());
    }

    for (Element draggable : ctx.getSelectedDraggables()) {
      if (options.getDraggableHoverClass() != null) {
        DraggableHandler dragHandler = DraggableHandler.getInstance(draggable);
        dragHandler.getHelper().removeClass(options.getDraggableHoverClass());
        $(draggable).removeData(options.getDraggableHoverClass());
      }

      trigger(new DeactivateDroppableEvent(ctx), options.getOnDeactivate(), ctx);
    }

  }

  public void drag(DragAndDropContext ctx, GqEvent e) {
    Element droppable = ctx.getDroppable();
    Element draggable = ctx.getDraggable();

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
    DragAndDropContext parentDndContext = null;
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
        parentDndContext = new DragAndDropContext(ctx, droppableParents.get(0));
      }
    }

    if (parentDroppableHandler != null && c == PositionStatus.IS_OVER) {
      parentDroppableHandler.isOver = false;
      parentDroppableHandler.isOut = true;
      parentDroppableHandler.out(parentDndContext, e);
    }

    if (c == PositionStatus.IS_OUT) {
      isOut = true;
      isOver = false;
      out(ctx, e);
    } else {
      isOver = true;
      isOut = false;
      over(ctx, e);
    }

    if (parentDroppableHandler != null && c == PositionStatus.IS_OUT) {
      parentDroppableHandler.isOut = false;
      parentDroppableHandler.isOver = true;
      parentDroppableHandler.over(parentDndContext, e);
    }

  }

  public boolean drop(final DragAndDropContext ctx, boolean alreadyDrop, GqEvent e) {

    Element draggable = ctx.getDraggable();

    if (options == null) {
      return false;
    }

    boolean drop = false;

    if (!options.isDisabled() && visible) {
      if (intersect(draggable) && !checkChildrenIntersection(ctx)
          && isDraggableAccepted(ctx)) {

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

      if (drop || isDraggableAccepted(ctx)) {
        isOut = true;
        isOver = false;
        deactivate(ctx, e);
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

  public void out(DragAndDropContext ctx, GqEvent e) {
    Element droppable = ctx.getDroppable();
    Element currentDraggable = ctx.getDraggable();

    if (currentDraggable == null || currentDraggable == droppable) {
      return;
    }

    if (isDraggableAccepted(ctx)) {
      if (options.getDroppableHoverClass() != null) {
        droppable.removeClassName(options.getDroppableHoverClass());
      }

      if (options.getDraggableHoverClass() != null) {
        for (Element draggable : ctx.getSelectedDraggables()) {
          Integer counter = $(draggable).data(options.getDraggableHoverClass(),
              Integer.class);
          $(draggable).data(options.getDraggableHoverClass(),
              new Integer(--counter));
          if (counter == 0) {
            DraggableHandler dragHandler = DraggableHandler
                .getInstance(draggable);
            dragHandler.getHelper().removeClass(
                options.getDraggableHoverClass());
          }
        }
      }

      trigger(new OutDroppableEvent(ctx), options.getOnOut(), ctx);
    }
  }

  public void over(DragAndDropContext ctx, GqEvent e) {
    Element droppable = ctx.getDroppable();
    Element currentDraggable = ctx.getDraggable();

    if (currentDraggable == null || currentDraggable == droppable) {
      return;
    }

    if (isDraggableAccepted(ctx)) {
      if (options.getDroppableHoverClass() != null) {
        droppable.addClassName(options.getDroppableHoverClass());
      }
      if (options.getDraggableHoverClass() != null) {
        for (Element draggable : ctx.getSelectedDraggables()) {
          DraggableHandler dragHandler = DraggableHandler
              .getInstance(draggable);
          dragHandler.getHelper().addClass(options.getDraggableHoverClass());
          Integer counter = $(draggable).data(options.getDraggableHoverClass(),
              Integer.class);
          $(draggable).data(options.getDraggableHoverClass(),
              new Integer(++counter));
        }
      }

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

  private boolean checkChildrenIntersection(DragAndDropContext ctx) {
    Element droppable = ctx.getDroppable();
    Element draggable = ctx.getDraggable();

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
          && handler.isDraggableAccepted(ctx) && handler.intersect(draggable)) {
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

    int draggableLeft = dragHandler.getAbsolutePosition().left;
    int draggableRight = draggableLeft
        + dragHandler.getHelperDimension().getWidth();
    int draggableTop = dragHandler.getAbsolutePosition().top;
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

  private boolean isDraggableAccepted(DragAndDropContext ctx) {
    AcceptFunction accept = options.getAccept();
    return accept != null && accept.acceptDrop(ctx);
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

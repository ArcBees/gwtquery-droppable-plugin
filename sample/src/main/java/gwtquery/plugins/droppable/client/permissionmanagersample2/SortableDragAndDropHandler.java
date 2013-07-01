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
package gwtquery.plugins.droppable.client.permissionmanagersample2;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.query.client.GQuery;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;
import gwtquery.plugins.droppable.client.gwt.DragAndDropCellList;
import gwtquery.plugins.droppable.client.permissionmanagersample2.MemberDatabase.MemberInfo;

import static com.google.gwt.query.client.GQuery.$;

/**
 * All logic of the drag and drop operation is contained in this class.
 *
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 *
 */
public class SortableDragAndDropHandler<T> implements DropEventHandler,
    OverDroppableEventHandler, OutDroppableEventHandler, DragEventHandler {

  private HandlerRegistration dragHandlerRegistration;
  private GQuery placeHolder;
  private DragAndDropCellList<T> cellList;
  private Element currentDroppable;
  private Element currentDraggable;
  private int dropMode;

  public SortableDragAndDropHandler(DragAndDropCellList<T> cellList) {
    this.cellList = cellList;
    placeHolder = $("<div style='height:1px; border-bottom: dotted 1px blue;'>&nbsp;</div>");
  }

  /**
   * When draggable is dragging inside the panel, check if the place holder has
   * to move
   */
  public void onDrag(DragEvent event) {
    maybeMovePlaceHolder();
  }

  /**
   * On drop, insert the draggable at the place holder index, remove handler on
   * the {@link DragEvent} of this draggable and remove the visual place holder
   */
  public void onDrop(DropEvent event) {
    // TODO implement drop !
    reset();

  }

  /**
   * When a draggable is out the panel, remove handler on the {@link DragEvent}
   * of this draggable and remove the visual place holder
   */
  public void onOutDroppable(OutDroppableEvent event) {
    reset();
  }

  /**
   * When a draggable is being over the panel, listen on the {@link DragEvent}
   * of the draggable and put a visual place holder.
   */
  public void onOverDroppable(final OverDroppableEvent event) {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      public void execute() {
        onOver(event);
      }
    });
  }

  public void onOver(OverDroppableEvent event) {
    if (currentDroppable != null) {
      reset();
    }

    Object droppable = event.getDroppableData();

    if (!(droppable instanceof MemberInfo)) {
      return;
    }

    currentDroppable = event.getDroppable();
    currentDraggable = event.getDragHelper();
    // create a place holder
    maybeMovePlaceHolder();
    // listen drag event when draggable is over the droppable
    dragHandlerRegistration = cellList.addDragHandler(this);
  }

  /**
   * Check if we have to move the place holder
   *
   * @param draggableHelper
   */
  private void maybeMovePlaceHolder() {
    $(currentDroppable).removeClass(Resource.INSTANCE.css().droppableHover());

    int droppableHeight = $(currentDroppable).outerHeight();
    int droppableTop = $(currentDroppable).offset().top;
    int droppableBottom = droppableTop + droppableHeight;

    int draggableHeight = $(currentDraggable).outerHeight();
    int draggableTop = $(currentDraggable).offset().top;
    int draggableBottom = draggableTop + draggableHeight;

    // if more than 25% (vertical axis) of the draggable is out of the droppable, the drop will occurs between the cells
    double treshold = .25 * draggableHeight;

    if (droppableTop - draggableTop >  treshold) {
      // drop will occurs above the current droppable
      placeHolder.insertBefore(currentDroppable);
      dropMode = -1;
    } else if (draggableBottom - droppableBottom > treshold) {
      // drop will occurs below the current droppable
      placeHolder.insertAfter(currentDroppable);
      dropMode = 0;
    } else {
      // drop will occurs on the current droppable
      placeHolder.remove();
      $(currentDroppable).addClass(Resource.INSTANCE.css().droppableHover());
      dropMode = 1;
    }
  }

  private void reset() {
    // don't listen drag event on the draggable
    if (dragHandlerRegistration != null) {
      dragHandlerRegistration.removeHandler();
    }
    dragHandlerRegistration = null;

    // remove the place holder
    placeHolder.remove();

    // reset currentDraggable and currentDroppable
    $(currentDroppable).removeClass(Resource.INSTANCE.css().droppableHover());

    currentDraggable = null;
    currentDroppable = null;
  }
}

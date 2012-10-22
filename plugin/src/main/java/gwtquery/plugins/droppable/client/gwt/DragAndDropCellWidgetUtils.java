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
import static gwtquery.plugins.draggable.client.Draggable.Draggable;
import static gwtquery.plugins.droppable.client.Droppable.Droppable;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.query.client.GQuery;

import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.droppable.client.DroppableHandler;
import gwtquery.plugins.droppable.client.DroppableOptions;

/**
 * Utils class with all code used in CellWidget to manage
 * the drag and drop behavior of cells
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 */

public class DragAndDropCellWidgetUtils {

  public static final String VALUE_KEY = "__dragAndDropCellAssociatedValue";

  private static final DragAndDropCellWidgetUtils INSTANCE = new DragAndDropCellWidgetUtils();

  static DragAndDropCellWidgetUtils get() {
    return INSTANCE;
  }

  private DragAndDropCellWidgetUtils() {
  }

  void cleanCell(Element cell) {

    if (cell == null) {
      return;
    }

    GQuery $cell = $(cell);

    if (DraggableHandler.getInstance(cell) != null) {
      $cell.as(Draggable).destroy();
    }

    if (DroppableHandler.getInstance(cell) != null) {
      $cell.as(Droppable).destroy();
    }
    
    $cell.removeData(VALUE_KEY);
  }

  <C> void maybeMakeDraggableOrDroppable(Element cell, C value,
      CellDragAndDropBehaviour<C> cellDragAndDropBehaviour,
      DraggableOptions draggableOptions, DroppableOptions droppableOptions,
      EventBus eventBus) {

    GQuery $cell = $(cell);

    if ((cellDragAndDropBehaviour == null || cellDragAndDropBehaviour
        .isDraggable(value))
        && DraggableHandler.getInstance(cell) == null) {
      
      $cell.as(Draggable).draggable(draggableOptions, eventBus);
    
    }

    if ((cellDragAndDropBehaviour == null || cellDragAndDropBehaviour
        .isDroppable(value))
        && DroppableHandler.getInstance(cell) == null) {
      
      $cell.as(Droppable).droppable(droppableOptions, eventBus);
    
    }

    $cell.data(VALUE_KEY, value);
  }

}

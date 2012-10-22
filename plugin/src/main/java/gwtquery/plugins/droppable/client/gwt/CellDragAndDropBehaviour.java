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

/**
 * This objet determines if the current rendering cell have to be draggable
 * and/or droppable
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 * @param <C>
 *          the cell type
 */
public interface CellDragAndDropBehaviour<C> {

  /**
   * Implementation of {@link CellDragAndDropBehaviour} definig cells as
   * draggable only
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   * @param <C>
   */
  public class CellDragOnlyBehaviour<C> implements CellDragAndDropBehaviour<C> {

    public boolean isDraggable(C value) {
      return true;
    }

    public boolean isDroppable(C value) {
      return false;
    }

  }

  /**
   * Implementation of {@link CellDragAndDropBehaviour} defining cells as
   * droppable only
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
   * 
   * @param <C>
   */
  public class CellDropOnlyBehaviour<C> implements CellDragAndDropBehaviour<C> {

    public boolean isDraggable(C value) {
      return false;
    }

    public boolean isDroppable(C value) {
      return true;
    }

  }

  /**
   * This method is called during the render of a cell. It decides if the cell
   * is draggable or not.
   * 
   * @param value
   * @param key
   * @return
   */
  boolean isDraggable(C value);

  /**
   * This method is called during the render of a cell. It decides if the cell
   * is droppable or not.
   * 
   * @param value
   * @param key
   * @return
   */
  boolean isDroppable(C value);
}
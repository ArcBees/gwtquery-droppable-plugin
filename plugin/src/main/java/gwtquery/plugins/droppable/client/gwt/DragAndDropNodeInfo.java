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
 */package gwtquery.plugins.droppable.client.gwt;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel.DefaultNodeInfo;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.gwt.CellDragAndDropBehaviour.CellDragOnlyBehaviour;
import gwtquery.plugins.droppable.client.gwt.CellDragAndDropBehaviour.CellDropOnlyBehaviour;

/**
 * {@link DefaultNodeInfo} implementation allowing dragging and dropping of
 * nodes.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com, @jdramaix)
 * 
 * @param <T>
 *          the node type
 */
public class DragAndDropNodeInfo<T> extends DefaultNodeInfo<T> {

  /**
   * {@link CellDragAndDropBehaviour} used to determine if node have to be
   * droppable and/or draggable
   */
  private CellDragAndDropBehaviour<T> cellDragAndDropBehaviour;

  /**
   * The options used for draggable node.
   */
  private DraggableOptions draggableOptions;

  /**
   * The options used for droppable node.
   */
  private DroppableOptions droppableOptions;

  /**
   * Construct a new {@link DragAndDropNodeInfo}.
   * 
   * @param cell
   *          the Cell used by this Node
   * @param cellDragAndDropBehaviour
   *          an instance of {@link CellDragAndDropBehaviour} or null if you
   *          want that all nodes are draggable and droppable.
   */
  public DragAndDropNodeInfo(AbstractDataProvider<T> dataProvider, Cell<T> cell) {
    this(dataProvider, cell, null, null);
  }

  /**
   * Construct a new {@link DragAndDropNodeInfo}.
   * 
   * @param dataProvider
   *          the {@link AbstractDataProvider} that provides the child values
   * @param cell
   *          the {@link Cell} used to render the child values update when the
   *          selection changes
   * @param selectionModel
   *          the {@link SelectionModel} used for selection
   * @param valueUpdater
   *          the {@link ValueUpdater}
   */
  public DragAndDropNodeInfo(AbstractDataProvider<T> dataProvider,
      Cell<T> cell, SelectionModel<? super T> selectionModel,
      ValueUpdater<T> valueUpdater) {

    this(dataProvider, cell, selectionModel, valueUpdater, null);
  }

  /**
   * Construct a new {@link DragAndDropNodeInfo}.
   * 
   * @param dataProvider
   *          the {@link AbstractDataProvider} that provides the child values
   * @param cell
   *          the {@link Cell} used to render the child values update when the
   *          selection changes
   * @param selectionModel
   *          the {@link SelectionModel} used for selection
   * @param valueUpdater
   *          the {@link ValueUpdater}
   * @param cellDragAndDropBehaviour
   *          an instance of {@link CellDragAndDropBehaviour} or null if you
   *          want that all nodes are draggable and droppable.
   */
  public DragAndDropNodeInfo(AbstractDataProvider<T> dataProvider,
      Cell<T> cell, SelectionModel<? super T> selectionModel,
      ValueUpdater<T> valueUpdater,
      CellDragAndDropBehaviour<T> cellDragAndDropBehaviour) {

    super(dataProvider, cell, selectionModel, valueUpdater);
    this.cellDragAndDropBehaviour = cellDragAndDropBehaviour;
    this.draggableOptions = new DraggableOptions();
    this.droppableOptions = new DroppableOptions();
  }

  /**
   * 
   * @return the {@link CellDragAndDropBehaviour}
   */
  public CellDragAndDropBehaviour<T> getCellDragAndDropBehaviour() {
    return cellDragAndDropBehaviour;
  }

  /**
   * 
   * @return the {@link DraggableOptions} used to make cells draggable
   */
  public DraggableOptions getDraggableOptions() {
    return draggableOptions;
  }

  /**
   * 
   * @return the {@link DroppableOptions} used to make cells droppable
   */
  public DroppableOptions getDroppableOptions() {
    return droppableOptions;
  }

  /**
   * Set the {@link CellDragAndDropBehaviour}. If null is given, all cells will
   * be draggable and droppable
   * 
   * @param cellDragAndDropBehaviour
   */
  public void setCellDragAndDropBehaviour(
      CellDragAndDropBehaviour<T> cellDragAndDropBehaviour) {
    this.cellDragAndDropBehaviour = cellDragAndDropBehaviour;
  }

  /**
   * By invoking this method, the cells will be draggable only
   */
  public void setCellDraggableOnly() {
    cellDragAndDropBehaviour = new CellDragOnlyBehaviour<T>();

  }

  /**
   * By invoking this method, the cells will be droppable only
   */
  public void setCellDroppableOnly() {
    cellDragAndDropBehaviour = new CellDropOnlyBehaviour<T>();
  }

  /**
   * Set the {@link DraggableOptions} used to make cells draggable
   */
  public void setDraggableOptions(DraggableOptions draggableOptions) {
    this.draggableOptions = draggableOptions;
  }

  /**
   * Set the {@link DroppableOptions} used to make cells droppable
   */
  public void setDroppableOptions(DroppableOptions droppableOptions) {
    this.droppableOptions = droppableOptions;
  }
}

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
package gwtquery.plugins.droppable.client.gfindersample;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.gfindersample.FileSystem.File;
import gwtquery.plugins.droppable.client.gfindersample.FileSystem.FileType;
import gwtquery.plugins.droppable.client.gwt.CellDragAndDropBehaviour;
import gwtquery.plugins.droppable.client.gwt.DragAndDropNodeInfo;

/**
 * Tree view model used in this example. This model use
 * {@link DragAndDropNodeInfo} to add drag and drop support.
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class FileTreeViewModel implements TreeViewModel {

  /**
   * The cell used to render files.
   */
  private static class FileCell extends AbstractCell<File> {

    @Override
    public void render(File value, Object key, SafeHtmlBuilder sb) {
      if (value != null) {
        sb.appendHtmlConstant("<div class='" + getCssClassName(value) + "'></div><div style='padding-top:3px;'>");
        sb.appendEscaped(value.getName());
        sb.appendHtmlConstant("</div>");
      }
    }

    private String getCssClassName(File value) {
      if (value.getType() == FileType.DIRECTORY) {
        return Resource.INSTANCE.css().directoryCell();
      }

      return Resource.INSTANCE.css().fileCell();
    }
  }

  private final Cell<File> fileCell;

  public FileTreeViewModel() {
    fileCell = new FileCell();
  }

  public <T> NodeInfo<?> getNodeInfo(T value) {

    if (value == null) {
      // Return root directories
      DragAndDropNodeInfo<File> nodeInfo = new DragAndDropNodeInfo<File>(
          FileSystem.get().getRootDirectories(), fileCell);
      // root directory cannot be dragged
      nodeInfo.setCellDroppableOnly();
      // setup drop operation
      nodeInfo.setDroppableOptions(createDroppableOptions());

      return nodeInfo;

    } else if (value instanceof File) {

      File dir = (File) value;
      assert dir.getType() == FileType.DIRECTORY;

      ListDataProvider<File> files = FileSystem.get().ls(dir);

      DragAndDropNodeInfo<File> nodeInfo = new DragAndDropNodeInfo<File>(files,
          fileCell);
      // setup drop operation
      nodeInfo.setDroppableOptions(createDroppableOptions());
      // setup drag operation
      nodeInfo.setDraggableOptions(createDraggableOptions());
      // all cells can be draggable. Only cell containing a directory can be
      // droppable
      nodeInfo
          .setCellDragAndDropBehaviour(new CellDragAndDropBehaviour<File>() {

            public boolean isDraggable(File value) {
              return true;
            }

            public boolean isDroppable(File value) {
              // only directories can be droppable
              return value.getType() == FileType.DIRECTORY;
            }

          });

      return nodeInfo;

    }

    // Unhandled type.
    String type = value.getClass().getName();
    throw new IllegalArgumentException("Unsupported object type: " + type);
  }

  public boolean isLeaf(Object value) {
    File f = (File) value;
    return f.getType() == FileType.FILE;
  }

  /**
   * Configure drag operation
   * 
   * @return
   */
  private DraggableOptions createDraggableOptions() {
    DraggableOptions options = new DraggableOptions();
    // use a clone of the original cell as drag helper
    options.setHelper(HelperType.CLONE);
    // set the opacity of the drag helper
    options.setOpacity((float) 0.8);
    // set the cursor to use during the drag operation
    options.setCursor(Cursor.POINTER);
    // append the drag helper to the CellBrowser
    options.setAppendTo("#fileBrowser");
    // constrain the drag operation inside the CellBrowser
    options.setContainment("#fileBrowser");

    return options;
  }

  /**
   * Configure drop operation
   * 
   * @return
   */
  private DroppableOptions createDroppableOptions() {
    DroppableOptions options = new DroppableOptions();
    // css class set to the droppable when an acceptable draggable is over it
    options.setDroppableHoverClass(Resource.INSTANCE.css().droppableHover());
    // css class set to the draggable when it is over a droppable
    options.setDraggableHoverClass(Resource.INSTANCE.css().dragHover());

    // a directory cannot be drop in one of these children
    options.setAccept(new AcceptFunction() {

      public boolean acceptDrop(DragAndDropContext ctx) {
        File dropDirectory = ctx.getDroppableData();
        File dragFile = ctx.getDraggableData();
        if (dragFile.getType() == FileType.FILE) {
          return true;
        }

        // check that the dropDirectory is not a child of dragFile
        File parent = dropDirectory.getParent();
        while (parent != null) {
          if (parent.equals(dragFile)) {
            return false;
          }
          parent = parent.getParent();
        }

        return true;
      }
    });

    return options;
  }

}

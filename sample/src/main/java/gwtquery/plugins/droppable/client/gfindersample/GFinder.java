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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.TreeViewModel.NodeInfo;

import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.gfindersample.FileSystem.File;
import gwtquery.plugins.droppable.client.gwt.DragAndDropCellBrowser;
import gwtquery.plugins.droppable.client.gwt.DragAndDropNodeInfo;

/**
 * This sample shows how to implement drag an drop in CellBrowser.
 * 
 * Just use a {@link DragAndDropCellBrowser} instead of {@link CellBrowser} and
 * {@link DragAndDropNodeInfo} instead of {@link NodeInfo}
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class GFinder implements EntryPoint {

  public void onModuleLoad() {
    Resource.INSTANCE.css().ensureInjected();

    RootPanel.get("cellBrowser").add(createFinder());

  }

  /**
   * Create the DragAndDropCellBrowser
   * 
   * @return
   */
  private DragAndDropCellBrowser createFinder() {
    // Create a DragAndDropCellBrowser
    DragAndDropCellBrowser cellBrowser = new DragAndDropCellBrowser(
        new FileTreeViewModel(), null);
    // define an id for the cellBrowser to use it as containment during drag
    // operation
    cellBrowser.getElement().setId("fileBrowser");
    cellBrowser.setAnimationEnabled(true);
    cellBrowser.addStyleName(Resource.INSTANCE.css().finder());
    cellBrowser.setDefaultColumnWidth(250);

    // add a drop handler to catch drop event
    cellBrowser.addDropHandler(new DropEventHandler() {

      public void onDrop(DropEvent event) {
        // retrieve dragged file
        File draggedFile = event.getDraggableData();
        // retrieve the directory where the file was dropped
        File destinationDir = event.getDroppableData();
        // Move the file
        FileSystem.get().moveFile(draggedFile, destinationDir);

      }
    });
    return cellBrowser;
  }

}

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
package gwtquery.plugins.droppable.client.gwtsimplesample;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

/**
 * Example showing how it is easy to add drag and drop functionality to GWT
 * Widget
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class GwtSimpleSample implements EntryPoint {

  interface Css extends CssResource {
    String droppableHover();

    String trashBin();
    String resetButton();
  }

  interface Resources extends ClientBundle {
    Resources INSTANCE = GWT.create(Resources.class);

    @Source("style.css")
    Css css();

    ImageResource gwtLogo();
  }

  public void onModuleLoad() {

    Resources.INSTANCE.css().ensureInjected();

    RootPanel.get("grid").add(createImageGrid());
    RootPanel.get("trashBin").add(createTrashBin());

    addResetButton();

  }

  private void addResetButton() {
    Button resetButton = new Button("Reset");
    resetButton.addStyleName(Resources.INSTANCE.css().resetButton());
    resetButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        RootPanel.get("grid").clear();
        RootPanel.get("trashBin").clear();
        onModuleLoad();
      }
    });

    RootPanel.get("trashBin").add(resetButton);

  }

  private Widget createDraggableImage() {
    Image gwtLogo = new Image(Resources.INSTANCE.gwtLogo());
    // make the imgae draggable
    DraggableWidget<Image> draggableLogo = new DraggableWidget<Image>(gwtLogo);
    // revert the image to its initial position if no drop
    draggableLogo.setRevert(RevertOption.ON_INVALID_DROP);
    // ensure that dragging image is above the others during drag operation
    draggableLogo.setDraggingZIndex(100);
    // set the cursor during the drag operation
    draggableLogo.setDraggingCursor(Cursor.MOVE);
    return draggableLogo;
  }

  /**
   * Create a 5x5 grid. Each cell contains a draggable gwt logo.
   * 
   * @return
   */
  private Widget createImageGrid() {

    Grid grid = new Grid(5, 5);
    CellFormatter cellFormatter = grid.getCellFormatter();
    
    int numRows = grid.getRowCount();
    int numColumns = grid.getColumnCount();

    
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numColumns; col++) {
        //add draggable logo
        grid.setWidget(row, col, createDraggableImage());
        
        //add size to cell in order to keep place when image will be removed
        cellFormatter.setHeight(row, col,"50px");
        cellFormatter.setWidth(row, col,"61px");
      }
    }

    return grid;
  }

  /**
   * Simply create a {@link Label} and make it droppable
   * 
   * @return
   */
  private Widget createTrashBin() {

    final Label trashBinLabel = new Label("Feed me !");
    trashBinLabel.addStyleName(Resources.INSTANCE.css().trashBin());

    // make the label droppable
    DroppableWidget<Label> trashBin = new DroppableWidget<Label>(trashBinLabel);
    // specify wich class has to be used when a draggable widget is over the
    // droppable
    trashBin.setDroppableHoverClass(Resources.INSTANCE.css().droppableHover());
    // add a drop handler
    trashBin.addDropHandler(new DropEventHandler() {

      int counter = 0;

      public void onDrop(DropEvent event) {
        Widget droppedImage = event.getDraggableWidget();
        droppedImage.removeFromParent();
        counter++;
        trashBinLabel.setText("I ate " + counter + " image"
            + (counter > 1 ? "s" : ""));

      }
    });

    // return the droppable widget
    return trashBin;
  }

}

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
package gwtquery.plugins.droppable.client.greedysample;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.Draggable.Draggable;
import static gwtquery.plugins.droppable.client.Droppable.Droppable;

import com.google.gwt.core.client.EntryPoint;

import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;

/**
 * Example showing the importance of the greddy option
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class GreedySample implements EntryPoint {

  public void onModuleLoad() {

    $("#draggable").as(Draggable).draggable();

    $("#mainDroppable1").as(Droppable).droppable(createDroppableOptions(false));
    $("#innerDroppable1").as(Droppable)
        .droppable(createDroppableOptions(false));
    $("#mainDroppable2").as(Droppable).droppable(createDroppableOptions(false));
    $("#innerDroppable2").as(Droppable).droppable(createDroppableOptions(true));

  }

  /**
   * Create droppable options. Setup some visual feedback on drop operation
   * 
   * @return
   */
  private DroppableOptions createDroppableOptions(final boolean greedy) {
    DroppableOptions options = new DroppableOptions();
    // class add to the droppable when the droppable is active
    options.setActiveClass("white-background");
    // class add to the droppable when a acceptable draggable is over the
    // droppable
    options.setDroppableHoverClass("yellow-background");
    // set the greddy option
    options.setGreedy(greedy);

    // method called when a acceptable draggable is dropped on the droppable
    options.setOnDrop(new DroppableFunction() {
      public void f(DragAndDropContext context) {
        $("#" + context.getDroppable().getId() + " > p").html(
            "The draggable was dropped on me");
        context.getDroppable().addClassName("orange-background");
      }
    });

    // method called when the droppable is activated
    options.setOnActivate(new DroppableFunction() {
      public void f(DragAndDropContext context) {
        $("#" + context.getDroppable().getId() + " > p").html(
            "I'm the " + context.getDroppable().getId() + " and I'm "
                + (greedy ? "greddy" : "not greddy"));
        context.getDroppable().removeClassName("orange-background");
      }
    });
    return options;
  }

}

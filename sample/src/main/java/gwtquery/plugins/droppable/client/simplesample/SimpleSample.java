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
package gwtquery.plugins.droppable.client.simplesample;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.Draggable.Draggable;
import static gwtquery.plugins.droppable.client.Droppable.Droppable;

import com.google.gwt.core.client.EntryPoint;

import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;

/**
 * Make any elements droppable !
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class SimpleSample implements EntryPoint {

  public void onModuleLoad() {

    $("#draggable").as(Draggable).draggable();

    // create droppable options
    DroppableOptions options = new DroppableOptions();

    // function called on when a acceptable draggable is dropped on the
    // droppable
    options.setOnDrop(new DroppableFunction() {
      public void f(DragAndDropContext context) {
        $(context.getDroppable()).addClass("orange-background").find("p").html(
            "Dropped !");

      }
    });
    
    //make the element droppable
    $("#droppable").as(Droppable).droppable(options);
  }

  

}

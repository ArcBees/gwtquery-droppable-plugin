package gwtquery.plugins.droppable.client.miscellanious;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.EntryPoint;

import gwtquery.plugins.droppable.client.Droppable;
import gwtquery.plugins.droppable.client.DroppableOptions;

import gwtquery.plugins.draggable.client.DragAndDropManager;

import gwtquery.plugins.draggable.client.DraggableOptions;

import gwtquery.plugins.draggable.client.Draggable;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;

public class Issue7Test implements EntryPoint {

  public void onModuleLoad() {
    DraggableOptions options = new DraggableOptions();
    options.setZIndex(1000);
    $("#draggable").as(Draggable.Draggable).draggable(options);
    
    $("#droppable1").as(Droppable.Droppable).droppable(createDroppableOptions(true));
    $("#droppable2").as(Droppable.Droppable).droppable(createDroppableOptions(false));
    $("#droppable3").as(Droppable.Droppable).droppable(createDroppableOptions(false));

  }

  private DroppableOptions createDroppableOptions(boolean open){
    DroppableOptions options = new DroppableOptions();
    options.setDroppableHoverClass("orange-background");
    if (open){
      options.setOnOver(new DroppableFunction() {
        
        public void f(DragAndDropContext context) {
          $("#droppable3,#droppable2").show();
          DragAndDropManager.getInstance().update(context);
        }
      });
    }
    
    return options;
    
  }
}

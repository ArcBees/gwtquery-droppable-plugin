package gwtquery.plugins.droppable.client.revertandacceptsample;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.Draggable.Draggable;
import static gwtquery.plugins.droppable.client.Droppable.Droppable;

import com.google.gwt.core.client.EntryPoint;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;

public class RevertAndAcceptSample implements EntryPoint {

  private static DroppableFunction DROP_FUNCTION = new DroppableFunction() {
    
    public void f(DragAndDropContext context) {
      $(context.getDroppable()).addClass("orange-background").find("p").html(
          "Dropped !");
    }
  };
  
  public void onModuleLoad() {
    
    DraggableOptions draggableOptions = new DraggableOptions();
    draggableOptions.setRevert(RevertOption.ON_VALID_DROP);
    $("#draggable1").as(Draggable).draggable(draggableOptions);
    
    draggableOptions = new DraggableOptions();
    draggableOptions.setRevert(RevertOption.ON_INVALID_DROP);
    $("#draggable2").as(Draggable).draggable(draggableOptions);
    
    
    DroppableOptions droppableOptions = new DroppableOptions();
    droppableOptions.setOnDrop(DROP_FUNCTION);
    droppableOptions.setActiveClass("blue-background");
    droppableOptions.setDraggableHoverClass("yellow-background");
    $("#droppable1").as(Droppable).droppable(droppableOptions);
    
    
    droppableOptions = new DroppableOptions();
    droppableOptions.setOnDrop(DROP_FUNCTION);
    droppableOptions.setActiveClass("blue-background");
    droppableOptions.setDraggableHoverClass("yellow-background");
    droppableOptions.setAccept("#draggable1");
    $("#droppable2").as(Droppable).droppable(droppableOptions);

  }

}

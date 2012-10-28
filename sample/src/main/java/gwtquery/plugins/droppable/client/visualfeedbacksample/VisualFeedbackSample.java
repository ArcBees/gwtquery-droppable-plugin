package gwtquery.plugins.droppable.client.visualfeedbacksample;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.Draggable.Draggable;
import static gwtquery.plugins.droppable.client.Droppable.Droppable;

import com.google.gwt.core.client.EntryPoint;

import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;

public class VisualFeedbackSample implements EntryPoint {

  private static DroppableFunction DROP_FUNCTION = new DroppableFunction() {
    
    public void f(DragAndDropContext context) {
      $(context.getDroppable()).addClass("orange-background").find("p").html(
          "Dropped !");
    }
  };
  
  public void onModuleLoad() {
    $("#draggable").as(Draggable).draggable();

    /*
     * Make droppable1 droppable
     */
    DroppableOptions options = new DroppableOptions();
    // class added to the droppable when a acceptable draggable is over it
    options.setDroppableHoverClass("yellow-background");
    options.setOnDrop(DROP_FUNCTION);
    $("#droppable1").as(Droppable).droppable(options);

    /*
     * Make droppable2 droppable
     */
    options = new DroppableOptions();
    // class added to the droppable when the droppable is activated
    options.setActiveClass("blue-background");
    options.setOnDrop(DROP_FUNCTION);
    $("#droppable2").as(Droppable).droppable(options);

    /*
     * Make droppable3 droppable
     */
    options = new DroppableOptions();
    // class added to an activate draggable when it is being dragged over the droppable
    options.setDraggableHoverClass("yellow-background");
    options.setOnDrop(DROP_FUNCTION);
    $("#droppable3").as(Droppable).droppable(options);

  }

}

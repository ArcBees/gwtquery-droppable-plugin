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
package gwtquery.plugins.droppable.client;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.user.client.Event.ONMOUSEDOWN;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.plugins.UiPlugin.Dimension;
import com.google.gwt.query.client.plugins.events.GqEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gwtquery.plugins.draggable.client.DragAndDropManager;
import gwtquery.plugins.draggable.client.DraggableHandler;
import gwtquery.plugins.droppable.client.Droppable.CssClassNames;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;

/**
 * Implementation of the {@link DragAndDropManager} for drop operations
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class DragAndDropManagerImpl extends DragAndDropManager {

  private Element currentDraggable;
  private Map<String, Collection<Element>> droppablesByScope;

  public DragAndDropManagerImpl() {
    // add default context
    droppablesByScope = new HashMap<String, Collection<Element>>();
    droppablesByScope.put("default", new ArrayList<Element>());
  }

  /**
   * Link a droppable with the specified scope <code>scope</code>
   * 
   * @param droppable
   * @param scope
   */
  @Override
  public void addDroppable(Element droppable, String scope) {
    Collection<Element> droppables = droppablesByScope.get(scope);
    if (droppables == null) {
      droppables = new ArrayList<Element>();
      droppablesByScope.put(scope, droppables);
    }
    droppables.add(droppable);
  }

  /**
   * Method called when the draggable is being dragged
   * 
   * @param draggable
   * @param e
   */
  @Override
  public void drag(Element draggable, GqEvent e) {
    DraggableHandler draggableHandler = DraggableHandler.getInstance(draggable);
    Collection<Element> droppables = getDroppablesByScope(draggableHandler
        .getOptions().getScope());
    if (droppables == null || droppables.size() == 0) {
      return;
    }

    for (Element droppable : droppables) {
      DroppableHandler dropHandler = DroppableHandler.getInstance(droppable);
      dropHandler.drag(droppable, draggable, e);

    }
  }

  /**
   * Method called when the draggable was dropped
   * 
   * @param draggable
   * @param e
   * @return
   */
  @Override
  public boolean drop(Element draggable, GqEvent e) {
    boolean dropped = false;
    DraggableHandler draggableHandler = DraggableHandler.getInstance(draggable);
    Collection<Element> droppables = getDroppablesByScope(draggableHandler
        .getOptions().getScope());
    if (droppables == null || droppables.size() == 0) {
      return false;
    }

    for (Element droppable : droppables) {
      DroppableHandler droppableHandler = DroppableHandler
          .getInstance(droppable);
      dropped |= droppableHandler.drop(droppable, draggable, e, dropped);
    }

    return dropped;
  }

  /**
   * 
   * @return the current draggable element or null if no drag operation in
   *         progress
   */
  @Override
  public Element getCurrentDraggable() {
    return currentDraggable;
  }

  /**
   * Return the list of droppable elements with the scope <code>scope</code>
   * 
   * @param scope
   * @return
   */
  @Override
  public Collection<Element> getDroppablesByScope(String scope) {
    return droppablesByScope.get(scope);
  }

  @Override
  public void initialize(Element draggable, GqEvent e) {
    DraggableHandler draggableHandler = DraggableHandler.getInstance(draggable);
    Collection<Element> droppables = getDroppablesByScope(draggableHandler
        .getOptions().getScope());
    if (droppables == null || droppables.size() == 0) {
      return;
    }

    GQuery droppablesInsideDraggable = $(draggable).find(
        "." + CssClassNames.GWTQUERY_DROPPABLE).andSelf();

    for (Element droppable : droppables) {
      GQuery $droppable = $(droppable);
      DroppableHandler droppableHandler = DroppableHandler
          .getInstance(droppable);
      droppableHandler.reset();
      DroppableOptions droppableOptions = droppableHandler.getOptions();
      AcceptFunction accept = droppableOptions.getAccept();
      if (droppableOptions.isDisabled()
          || (accept != null && !accept.acceptDrop(new DragAndDropContext(
              draggable, droppable)))) {
        continue;
      }

      boolean mustContinue = false;
      for (Element el : droppablesInsideDraggable.elements()) {
        if (el == droppable) {
          // droppableHandler.setDroppableDimension(new Dimension(0, 0));
          mustContinue = true;
          break;
        }
      }
      if (mustContinue) {
        continue;
      }

      droppableHandler.setVisible(!"none".equals(droppable.getStyle()
          .getDisplay()));

      if (droppableHandler.isVisible()) {
        droppableHandler.setDroppableOffset($droppable.offset());
        droppableHandler.setDroppableDimension(new Dimension(droppable));
        if (e == null || e.getTypeInt() == ONMOUSEDOWN) {
          droppableHandler.activate(droppable, e);
        }
      }

    }

  }

  @Override
  public boolean isHandleDroppable() {
    return true;
  }

  /**
   * Set the current draggeble element
   * 
   * @param draggable
   */
  @Override
  public void setCurrentDraggable(Element draggable) {
    currentDraggable = draggable;
  }
  
 /* @Override
  public void update() {
    initialize(currentDraggable, null);
  }*/
}

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

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.plugins.UiPlugin;
import com.google.gwt.query.client.plugins.Plugin;

import gwtquery.plugins.draggable.client.DragAndDropManager;

/**
 * Droppable plugin for GwtQuery
 */
public class Droppable extends UiPlugin {

  /**
   * Css class used in this plugin
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  public static interface CssClassNames {
    String GWTQUERY_DROPPABLE = "gwtQuery-droppable";
    String GWTQUERY_DROPPABLE_DISABLED = "gwtQuery-droppable-disabled";

  }

  // A shortcut to the class
  public static final Class<Droppable> Droppable = Droppable.class;

  public static final String DROPPABLE_HANDLER_KEY = "droppableHandler";

  // Register the plugin in GQuery
  static {
    GQuery.registerPlugin(Droppable.class, new Plugin<Droppable>() {
      public Droppable init(GQuery gq) {
        return new Droppable(gq);
      }
    });
  }

  /**
   * Constructor
   * 
   * @param gq
   */
  public Droppable(GQuery gq) {
    super(gq);
  }

  
  /**
   * Change the scope of the selected elements.
   * 
   * @param newScope
   * @return
   */
  public Droppable changeScope(String newScope) {
    for (Element e : elements()) {
      DroppableHandler handler = DroppableHandler.getInstance(e);
      if (handler != null) {
        String oldScope = handler.getOptions().getScope();
        DragAndDropManager dndManager = DragAndDropManager.getInstance();
        dndManager.getDroppablesByScope(oldScope).remove(e);
        dndManager.getDroppablesByScope(newScope).add(e);
        handler.getOptions().setScope(newScope);
      }
    }
    return this;
  }

  /**
   * Remove the droppable behavior to the selectedelements. This method releases
   * resources used by the plugin and should be called when an element is
   * removed of the DOM.
   * 
   * @return
   */
  public Droppable destroy() {
    DragAndDropManager ddm = DragAndDropManager.getInstance();
    for (Element e : elements()) {
      DroppableHandler infos = DroppableHandler.getInstance(e);
      ddm.getDroppablesByScope(infos.getOptions().getScope()).remove(e);
      $(e).removeClass(CssClassNames.GWTQUERY_DROPPABLE,
          CssClassNames.GWTQUERY_DROPPABLE_DISABLED).removeData(
          DROPPABLE_HANDLER_KEY);
    }
    return this;
  }

  /**
   * Make the selected elements droppable with default options
   * 
   * @return
   */
  public Droppable droppable() {
    return droppable(new DroppableOptions());
  }

  /**
   * Make the selected elements draggable by using the
   * <code>droppableOptions</code>
   * 
   * @param droppableOptions
   *          options used to initialize the droppable
   * @return
   */
  public Droppable droppable(DroppableOptions droppableOptions) {
    return droppable(droppableOptions, null);
  }

  /**
   * Make the selected elements droppable with default options. All drop events
   * will be fired on the <code>eventBus</code>
   * 
   *@param eventBus
   *          The eventBus to use to fire events.
   * @return
   */
  public Droppable droppable(HasHandlers eventBus) {
    return droppable(new DroppableOptions(), eventBus);
  }

  /**
   * Make the selected elements droppable by using the
   * <code>droppableOptions</code>. All drop events will be fired on the
   * <code>eventBus</code>
   * 
   *@param eventBus
   *          The eventBus to use to fire events.
   * @param droppableOptions
   *          options used to initialize the droppable
   * @return
   */
  public Droppable droppable(DroppableOptions droppableOptions,
      HasHandlers eventBus) {

    DragAndDropManager ddm = DragAndDropManager.getInstance();

    for (Element e : elements()) {
      DroppableHandler handler = new DroppableHandler(droppableOptions,
          eventBus);
      handler.setDroppableDimension(new Dimension(e));
      $(e).data(DROPPABLE_HANDLER_KEY, handler);

      ddm.addDroppable(e, droppableOptions.getScope());

      e.addClassName(CssClassNames.GWTQUERY_DROPPABLE);

    }

    return this;
  }

  /**
   * Get the {@link DroppableOptions} for the first element.
   * 
   * @return
   */
  public DroppableOptions options() {
    if (length() > 0) {
      DroppableHandler handler = DroppableHandler.getInstance(get(0));
      if (handler != null) {
        return handler.getOptions();
      }
    }
    return null;
  }

  /**
   * Set the {@link DroppableOptions} for the selected elements.
   * 
   * @return
   */
  public Droppable options(DroppableOptions options) {
    if (length() > 0) {
      DroppableHandler handler = DroppableHandler.getInstance(get(0));
      if (handler != null) {
        handler.setOptions(options);
      }
    }
    return this;
  }

}

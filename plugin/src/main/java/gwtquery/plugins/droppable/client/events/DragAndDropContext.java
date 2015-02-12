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
package gwtquery.plugins.droppable.client.events;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.droppable.client.gwt.DragAndDropCellWidgetUtils.VALUE_KEY;

import com.google.gwt.dom.client.Element;
import gwtquery.plugins.draggable.client.events.DragContext;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

/**
 * Object containing usefull information on the drag and drop operations.
 *
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 */
public class DragAndDropContext extends DragContext {
    private Element droppable;

    public DragAndDropContext(DragContext ctx, Element droppable) {
        super(ctx);
        this.droppable = droppable;
    }

    /**
     * @return the current draggable DOM element
     */
    public Element getDroppable() {
        return droppable;
    }

    /**
     * This method allows getting the data object linked to the droppable element
     * (a cell) in the context of CellWidget. It returns the data object being
     * rendered by the droppable cell. Return null if we are not in the context of
     * an drag and drop cell widget.
     *
     * @param <T> the class of the data
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getDroppableData() {
        return (T) $(getDroppable()).data(VALUE_KEY);
    }

    /**
     * This method return the widget associated to the droppable DOM element if it
     * exist. It returns null otherwise.
     */
    public DroppableWidget<?> getDroppableWidget() {
        if (getDroppable() != null) {
            return DroppableWidget.get(getDroppable());
        }
        return null;
    }
}

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
package gwtquery.plugins.droppable.client.permissionmanagersample;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.cellview.client.CellTree.Style;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel.NodeInfo;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.DragFunction;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.events.DragContext;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.gwt.DragAndDropCellList;
import gwtquery.plugins.droppable.client.gwt.DragAndDropCellTree;
import gwtquery.plugins.droppable.client.gwt.DragAndDropNodeInfo;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;
import gwtquery.plugins.droppable.client.permissionmanagersample.MemberDatabase.MemberInfo;
import gwtquery.plugins.droppable.client.permissionmanagersample.MemberDatabase.Permission;

/**
 * This example shows how to implement drag and drop in a CellTree.
 * 
 * Just use a {@link DragAndDropCellTree} instead of a {@link CellTree} and
 * {@link DragAndDropNodeInfo} instead of {@link NodeInfo}
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class PermissionManagerSample implements EntryPoint {

  /**
   * The Cell used to render a {@link MemberInfo} with detailled info. Code
   * coming from the GWT showcase
   * 
   */
  static class DetailledMemberCell extends AbstractCell<MemberInfo> {

    /**
     * The html of the image used for contacts.
     * 
     */
    private final String imageHtml;

    public DetailledMemberCell() {
      this.imageHtml = AbstractImagePrototype.create(
          Resource.INSTANCE.contact()).getHTML();
    }

    @Override
    public void render(Context ctx, MemberInfo value, SafeHtmlBuilder sb) {
      // Value can be null, so do a null check..
      if (value == null) {
        return;
      }

      sb.appendHtmlConstant("<table>");

      // Add the contact image.
      sb.appendHtmlConstant("<tr><td rowspan='3'>");
      sb.appendHtmlConstant(imageHtml);
      sb.appendHtmlConstant("</td>");

      // Add the name and email address.
      sb.appendHtmlConstant("<td style='font-size:95%;'>");
      sb.appendEscaped(value.getFullName());
      sb.appendHtmlConstant("</td></tr><tr><td>");
      sb.appendEscaped(value.getEmailAddress());
      sb.appendHtmlConstant("</td></tr></table>");
    }
  }

  /**
   * Style used for our CellTree. Some style was modified to offer a greater
   * droppable area
   */
  interface DroppableCellTreeResource extends Resources {

    DroppableCellTreeResource INSTANCE = GWT
        .create(DroppableCellTreeResource.class);

    /**
     * The styles used in this widget.
     */
    @Source("DroppableCellTree.css")
    Style cellTreeStyle();
  }

  /**
   * Template for the helper
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  static interface Templates extends SafeHtmlTemplates {
    Templates INSTANCE = GWT.create(Templates.class);

    @Template("<div id='dragHelper' class='{0}'></div>")
    SafeHtml outerHelper(String cssClassName);
  }

  /**
   * Setup the drag operation for members cell. This method is static in order
   * to reuse it in MemberTreeViewModel class.
   * 
   * @param nodeInfo
   */
  static void configureDragOperation(DraggableOptions options) {

    // set a custom element as drag helper. The content of the helper will be
    // set when the drag will start
    options.setHelper($(Templates.INSTANCE.outerHelper(
        Resource.INSTANCE.css().dragHelper()).asString()));
    // opacity of the drag helper
    options.setOpacity((float) 0.9);
    // cursor during the drag operation
    options.setCursor(Cursor.MOVE);
    // the cell being greater than the helper, force the position of the
    // helper on the mouse cursor.
    options.setCursorAt(new CursorAt(10, 10, null, null));
    // append the helper to the body element
    options.setAppendTo("body");
    // set the revert option
    options.setRevert(RevertOption.ON_INVALID_DROP);
    // use a Function to fill the content of the helper
    // we could also add a DragStartEventHandler on the DragAndDropTreeCell and
    // DragAndDropCellList.
    options.setOnDragStart(new DragFunction() {
      public void f(DragContext context) {
        MemberInfo memberInfo = context.getDraggableData();
        context.getHelper().setInnerHTML(memberInfo.getEmailAddress());
      }
    });

  }

  public void onModuleLoad() {
    Resource.INSTANCE.css().ensureInjected();

    RootPanel.get("members").add(new Label("Others members"));
    RootPanel.get("members").add(createAllMemberList());

    RootPanel.get("permission").add(new Label("Project members"));
    RootPanel.get("permission").add(createPermissionTree());

  }

  /**
   * Create Panel with all members
   * 
   * @param contactForm
   * 
   * @return
   */
  private DroppableWidget<DragAndDropCellList<MemberInfo>> createAllMemberList() {

    // use a drag and drop cell list
    DragAndDropCellList<MemberInfo> cellList = new DragAndDropCellList<MemberInfo>(
        new DetailledMemberCell(), MemberDatabase.MemberInfo.KEY_PROVIDER);
    // setup CellList options
    cellList.setPageSize(30);
    cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
    final SingleSelectionModel<MemberInfo> selectionModel = new SingleSelectionModel<MemberInfo>(
        MemberDatabase.MemberInfo.KEY_PROVIDER);
    cellList.setSelectionModel(selectionModel);
    cellList.addStyleName(Resource.INSTANCE.css().memberList());

    // setup drag
    configureDragOperation(cellList.getDraggableOptions());
    // the cells are only draggable, the cellList will be droppable
    cellList.setCellDraggableOnly();

    MemberDatabase.get().addDataDisplay(cellList, Permission.NON_MEMBER);

    // make the cellList droppable in order to accept member.
    return makeCellListDroppable(cellList);
  }

  /**
   * Create the permission tree. Use a {@link DragAndDropCellTree} instead of a
   * {@link CellTree}. The drag and drop behavior will be configured thanks to
   * the {@link DragAndDropNodeInfo} in {@link MemberTreeViewModel} class
   * 
   * @return
   */
  private DragAndDropCellTree createPermissionTree() {

    DragAndDropCellTree cellTree = new DragAndDropCellTree(
        new MemberTreeViewModel(), null, DroppableCellTreeResource.INSTANCE);
    cellTree.setAnimationEnabled(true);
    cellTree.addStyleName(Resource.INSTANCE.css().permissionTree());

    return cellTree;

  }

  /**
   * Make the <code>cellList</code> droppable in order to accept Member.
   * 
   * @param cellList
   * @return
   */
  private DroppableWidget<DragAndDropCellList<MemberInfo>> makeCellListDroppable(
      DragAndDropCellList<MemberInfo> cellList) {

    DroppableWidget<DragAndDropCellList<MemberInfo>> droppableList = new DroppableWidget<DragAndDropCellList<MemberInfo>>(
        cellList);
    // setup drop
    droppableList.setDroppableHoverClass(Resource.INSTANCE.css().droppableHover());
    // drop handler
    droppableList.addDropHandler(new DropEventHandler() {

      public void onDrop(DropEvent event) {
        MemberInfo droppedMember = event.getDraggableData();
        MemberDatabase.get().permissionChange(droppedMember,
            Permission.NON_MEMBER);
      }

    });
    // Don't accept when a member coming from this cell list
    droppableList.setAccept(new AcceptFunction() {

      public boolean acceptDrop(DragAndDropContext context) {
        MemberInfo draggedMember = context.getDraggableData();
        // don't continue drop if member coming from this list
        return Permission.NON_MEMBER != draggedMember.getPermission();
      }

    });

    return droppableList;
  }

}

/*
 * Copyright 2010 Google Inc.
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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import gwtquery.plugins.droppable.client.DroppableOptions;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableFunction;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.gwt.DragAndDropNodeInfo;
import gwtquery.plugins.droppable.client.permissionmanagersample.MemberDatabase.MemberInfo;
import gwtquery.plugins.droppable.client.permissionmanagersample.MemberDatabase.Permission;

import java.util.List;

/**
 * The {@link TreeViewModel} used to organize members into a permission
 * hierarchy.
 */
public class MemberTreeViewModel implements TreeViewModel {

  /**
   * A Cell used to render the member inside the tree.
   */
  private static class MemberCell extends AbstractCell<MemberInfo> {

    @Override
    public void render(Context ctx, MemberInfo value, SafeHtmlBuilder sb) {
      if (value != null) {
        sb.appendEscaped(value.getFullName());
      }
    }
  }

  /**
   * The cell used to render permission.
   */
  private static class PermissionCell extends AbstractCell<Permission> {

    public PermissionCell() {
    }

    @Override
    public void render(Context ctx,  Permission value, SafeHtmlBuilder sb) {
      if (value != null) {
        sb.appendHtmlConstant("<div style='padding:15px 3px 3px 3px;' >");
        sb.appendEscaped(value.getDisplayName());
        sb.appendHtmlConstant("</div>");
      }
    }
  }

  private MemberCell memberCell;
  private PermissionCell permissionCell;
  private final ListDataProvider<Permission> permissionDataProvider;

  public MemberTreeViewModel() {

    permissionDataProvider = new ListDataProvider<Permission>();
    List<Permission> permissionList = permissionDataProvider.getList();

    for (Permission permission : MemberDatabase.get().queryPermissions()) {
      if (permission != Permission.NON_MEMBER) {
        permissionList.add(permission);
      }
    }

    permissionCell = new PermissionCell();
    memberCell = new MemberCell();

  }

  public <T> NodeInfo<?> getNodeInfo(T value) {

    if (value == null) {
      // permission tree node
      DragAndDropNodeInfo<Permission> permissionNodeInfo = new DragAndDropNodeInfo<Permission>(
          permissionDataProvider, permissionCell);

      // setup drop operation
      DroppableOptions options = permissionNodeInfo.getDroppableOptions();
      options.setDroppableHoverClass(Resource.INSTANCE.css().droppableHover());
      // use a DroppableFunction here. We can also add a DropHandler in the tree
      // itself
      options.setOnDrop(new DroppableFunction() {

        public void f(DragAndDropContext context) {
          MemberInfo droppedMember = context.getDraggableData();
          Permission dropPermission = context.getDroppableData();

          MemberDatabase.get().permissionChange(droppedMember, dropPermission);

        }
      });
      // permission cell are not draggable
      permissionNodeInfo.setCellDroppableOnly();
      return permissionNodeInfo;

    } else if (value instanceof Permission) {
      // member tree node
      Permission p = (Permission) value;

      ListDataProvider<MemberInfo> dataProvider = MemberDatabase.get()
          .getDataProvider(p);

      DragAndDropNodeInfo<MemberInfo> memberNodeInfo = new DragAndDropNodeInfo<MemberInfo>(
          dataProvider, memberCell, new SingleSelectionModel<MemberInfo>(),
          null);

      // setup the drag operation
      PermissionManagerSample.configureDragOperation(memberNodeInfo
          .getDraggableOptions());
      // member cell are only draggable
      memberNodeInfo.setCellDraggableOnly();

      return memberNodeInfo;
    }

    String type = value.getClass().getName();
    throw new IllegalArgumentException("Unsupported object type: " + type);
  }

  public boolean isLeaf(Object value) {
    return value instanceof MemberInfo;
  }

}

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
package gwtquery.plugins.droppable.client.gwt;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTreeNodeView;
import com.google.gwt.view.client.TreeViewModel.NodeInfo;

import java.util.List;

/**
 * A view of a tree node awith drag and drop support.
 * 
 * @param <T>
 *          the type that this view contains
 */
class DragAndDropCellTreeNodeView<T> extends CellTreeNodeView<T> {

  /**
   * The {@link com.google.gwt.view.client.HasData} used to show children. This
   * class is intentionally static because we might move it to a new
   * {@link DragAndDropCellTreeNodeView}, and we don't want non-static
   * references to the old {@link DragAndDropCellTreeNodeView}.
   * 
   * @param <C>
   *          the child item type
   */
  protected static class DragAndDropNodeCellList<C> extends NodeCellList<C> {

    /**
     * The view used by the NodeCellList.
     */
    protected class View extends NodeCellList<C>.View {

      private final Element childContainer;

      public View(Element childContainer) {
        
        super(childContainer);
        this.childContainer = childContainer;
      }

      @Override
      public void replaceAllChildren(List<C> values, SafeHtml html,
          boolean stealFocus) {
        // first clean all cell
        cleanAllCell();

        super.replaceAllChildren(values, html, stealFocus);

        // add drag and drop behaviour
        addDragAndDropBehaviour(values, 0);
      }

      @Override
      public void replaceChildren(List<C> values, int start, SafeHtml html,
          boolean stealFocus) {
        // clean cell before they are replaced
        int end = start + values.size();
        for (int rowIndex = start; rowIndex < end; rowIndex++) {
          Element oldCell = getRowElement(rowIndex);
          DragAndDropCellWidgetUtils.get().cleanCell(oldCell);
        }

        super.replaceChildren(values, start, html, stealFocus);

        // add drag and drop behaviour
        addDragAndDropBehaviour(values, start);
      }

      @SuppressWarnings("unchecked")
      protected void addDragAndDropBehaviour(List<C> values, int start) {

        int end = start + values.size();

        for (int rowIndex = start; rowIndex < end; rowIndex++) {
          C value = values.get(rowIndex - start);
          Element newCell = getRowElement(rowIndex);

          if (!(getNodeInfo() instanceof DragAndDropNodeInfo<?>)) {
            continue;
          }
          final DragAndDropNodeInfo<C> dndNodeInfo = (DragAndDropNodeInfo<C>) getNodeInfo();

          DragAndDropCellWidgetUtils.get().maybeMakeDraggableOrDroppable(
              newCell,
              value,
              dndNodeInfo.getCellDragAndDropBehaviour(),
              dndNodeInfo.getDraggableOptions(),
              dndNodeInfo.getDroppableOptions(),
              ((DragAndDropCellTreeNodeView) getNodeView()).getTree()
                  .ensureDragAndDropHandlers());
        }

      }

      protected void cleanAllCell() {
        $(childContainer).children().each(new Function() {
          @Override
          public void f(Element div) {
            DragAndDropCellWidgetUtils.get().cleanCell(
                (Element) div.getChild(0).cast());
          }
        });

      }

      private Element getRowElement(int indexOnPage) {
        if (indexOnPage >= 0 && childContainer.getChildCount() > indexOnPage) {
          return childContainer.getChild(indexOnPage).getChild(0).cast();
        }
        return null;
      }
    }

    // keep the view to clean it during the cleanup
    private View view;

    public DragAndDropNodeCellList(final NodeInfo<C> nodeInfo,
        final CellTreeNodeView<?> nodeView, int pageSize) {
      super(nodeInfo, nodeView, pageSize);
     
    }

    /**
     * Cleanup this node view.
     */
    public void cleanup() {
      super.cleanup();
      view.cleanAllCell();
    }

    @Override
    protected NodeCellList<C>.View createView() {
      if (view == null) {
        view = new View(getNodeView().ensureChildContainer());
      }
      return view;
    }

  }

  private DragAndDropCellTree tree;

  /**
   * Construct a {@link DragAndDropCellTreeNodeView}.
   * 
   * @param tree
   *          the parent {@link DragAndDropCellTreeNodeView}
   * @param parent
   *          the parent {@link DragAndDropCellTreeNodeView}
   * @param parentNodeInfo
   *          the {@link NodeInfo} of the parent
   * @param elem
   *          the outer element of this {@link DragAndDropCellTreeNodeView}
   * @param value
   *          the value of this node
   */
  public DragAndDropCellTreeNodeView(final DragAndDropCellTree tree,
      final CellTreeNodeView<?> parent, NodeInfo<T> parentNodeInfo,
      Element elem, T value) {
    super(tree, parent, parentNodeInfo, elem, value);
    this.tree = tree;
  }

  protected <C> CellTreeNodeView<C> createTreeNodeView(NodeInfo<C> nodeInfo,
      Element childElem, C childValue, Object viewData) {
    return new DragAndDropCellTreeNodeView<C>(tree, this, nodeInfo, childElem,
        childValue);
  }

  protected DragAndDropCellTree getTree() {
    return tree;
  }
  
  @Override
  protected <C> NodeCellList<C> createNodeCellList(
      NodeInfo<C> nodeInfo) {
    return new DragAndDropNodeCellList<C>(nodeInfo, this,
        tree.getDefaultNodeSize());
  }
}

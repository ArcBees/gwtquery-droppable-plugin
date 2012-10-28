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
package gwtquery.plugins.droppable.client.celltablesample;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStartEvent.DragStartEventHandler;
import gwtquery.plugins.droppable.client.celltablesample.ContactDatabase.Category;
import gwtquery.plugins.droppable.client.celltablesample.ContactDatabase.ContactInfo;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.gwt.DragAndDropCellTable;
import gwtquery.plugins.droppable.client.gwt.DragAndDropColumn;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

/**
 * Take the cell table example of the GWT showcase and make all cell droppable
 * and draggable
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class CellTableSample implements EntryPoint {

  /**
   * The constants used in this Content Widget.
   */
  public static interface CwConstants extends Constants {
    @DefaultStringValue(value = "Address")
    String cwCellTableColumnAddress();

    @DefaultStringValue(value = "Category")
    String cwCellTableColumnCategory();

    @DefaultStringValue(value = "First Name")
    String cwCellTableColumnFirstName();

    @DefaultStringValue(value = "Last Name")
    String cwCellTableColumnLastName();

  }

  interface CellTableSampleUiBinder extends UiBinder<Widget, CellTableSample> {
  }

  /**
   * Template for the helper
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  static interface Templates extends SafeHtmlTemplates {
    Templates INSTANCE = GWT.create(Templates.class);

    @Template("<div id='dragHelper' style='border:1px solid black; background-color:#ffffff; color:black; width:150px;'></div>")
    SafeHtml outerHelper();
  }

  /**
   * The Cell used to render a {@link ContactInfo}.
   * 
   * Code coming from the GWT showcase
   * 
   */
  private static class ContactCell extends AbstractCell<ContactInfo> {

    /**
     * The html of the image used for contacts.
     * 
     */
    private final String imageHtml;

    public ContactCell(ImageResource image) {
      this.imageHtml = AbstractImagePrototype.create(image).getHTML();
    }

    @Override
    public void render(Context context,
        ContactInfo value, SafeHtmlBuilder sb) {
      // Value can be null, so do a null check..
      if (value == null) {
        return;
      }


      sb.appendHtmlConstant("<table>");

      // Add the contact image.
      sb.appendHtmlConstant("<tr><td rowspan='3'>");
      sb.appendHtmlConstant(imageHtml);
      sb.appendHtmlConstant("</td>");

      // Add the name and address.
      sb.appendHtmlConstant("<td style='font-size:95%;'>");
      sb.appendEscaped(value.getFullName());
      sb.appendHtmlConstant("</td></tr><tr><td>");
      sb.appendEscaped(value.getAddress());
      sb.appendHtmlConstant("</td></tr></table>");
      
    }
  }

  private static CellTableSampleUiBinder uiBinder = GWT
      .create(CellTableSampleUiBinder.class);

  /**
   * The main CellTable. Use a {@link DragAndDropCellTable} instead of a
   * {@link CellTable}
   */
  @UiField(provided = true)
  DragAndDropCellTable<ContactInfo> cellTable;

  /**
   * The delete button
   */
  @UiField(provided = true)
  Button deleteButton;

  /**
   * The droppable "contact to delete" cell list.
   */
  @UiField(provided = true)
  DroppableWidget<CellList<ContactInfo>> exportCellList;

  /**
   * The pager used to change the range of data.
   */
  @UiField(provided = true)
  SimplePager pager;

  /**
   * The undo button
   */
  @UiField(provided = true)
  Button undoButton;

  /**
   * An instance of the constants.
   */
  private final CwConstants constants;

  public CellTableSample() {
    constants = GWT.create(CwConstants.class);
  }

  /**
   * Initialize this example.
   */
  public void onModuleLoad() {
    Resource.INSTANCE.css().ensureInjected();

    createDragAndDropCellTable();
    createDroppableList();

    // Create the UiBinder.
    Widget w = uiBinder.createAndBindUi(this);
    RootPanel.get("sample").add(w);
  }

  /**
   * This method create the CellTable for the contacts
   */
  private void createDragAndDropCellTable() {

    // Create a DragAndDropCellTable.
    cellTable = new DragAndDropCellTable<ContactInfo>(
        ContactDatabase.ContactInfo.KEY_PROVIDER);
    // Create a Pager to control the table.
    SimplePager.Resources pagerResources = GWT
        .create(SimplePager.Resources.class);
    pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
    pager.setDisplay(cellTable);

    // Add a selection model so we can select cells.
    final MultiSelectionModel<ContactInfo> selectionModel = new MultiSelectionModel<ContactInfo>(
        ContactDatabase.ContactInfo.KEY_PROVIDER);
    cellTable.setSelectionModel(selectionModel);
    
 // Attach a column sort handler to the ListDataProvider to sort the list.
    ListHandler<ContactInfo> sortHandler = new ListHandler<ContactInfo>(
        ContactDatabase.get().getDataProvider().getList());
    cellTable.addColumnSortHandler(sortHandler);


    // Initialize the columns.
    initTableColumns(selectionModel,sortHandler);

    // Add the CellList to the adapter in the database.
    ContactDatabase.get().addDataDisplay(cellTable);

    // fill the helper when the drag operation start
    cellTable.addDragStartHandler(new DragStartEventHandler() {

      public void onDragStart(DragStartEvent event) {
        ContactInfo contact = event.getDraggableData();
        Element helper = event.getHelper();
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        // reuse the contact cell to render the inner html of the drag helper.
        new ContactCell(Resource.INSTANCE.contact()).render(null, contact, sb);
        helper.setInnerHTML(sb.toSafeHtml().asString());

      }
    });

  }

  /**
   * Create a droppable CellList
   */
  private void createDroppableList() {
    // Create a ConcactCell
    ContactCell contactCell = new ContactCell(Resource.INSTANCE.contact());

    CellList<ContactInfo> cellList = new CellList<ContactInfo>(contactCell,
        ContactDatabase.ContactInfo.KEY_PROVIDER);
    cellList.addStyleName(Resource.INSTANCE.css().exportCellList());

    cellList.setPageSize(30);
    cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
    // temporary ListDataProvider to keep list of contacts to delete
    final ListDataProvider<ContactInfo> deleteContactList = new ListDataProvider<ContactInfo>();
    deleteContactList.addDataDisplay(cellList);

    // make the cell list droppable.
    exportCellList = new DroppableWidget<CellList<ContactInfo>>(cellList);

    // setup the drop operation
    exportCellList.setDroppableHoverClass(Resource.INSTANCE.css().droppableHover());
    exportCellList.setActiveClass(Resource.INSTANCE.css().droppableActive());
    exportCellList.addDropHandler(new DropEventHandler() {

      public void onDrop(DropEvent event) {
        ContactInfo contactToDelete = event.getDraggableData();
        // first remove the contact to the table
        ContactDatabase.get().removeContact(contactToDelete);
        // avoid doublon
        deleteContactList.getList().remove(contactToDelete);
        // add the contact to the delete list
        deleteContactList.getList().add(contactToDelete);

      }
    });

    // create delete button
    deleteButton = new Button("Delete contacts");
    deleteButton.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        deleteContactList.getList().clear();
        Window.alert("The contacts have been deleted");
      }
    });

    // create undo button
    undoButton = new Button("Undo");
    undoButton.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        for (ContactInfo c : deleteContactList.getList()) {
          ContactDatabase.get().addContact(c);
        }
        deleteContactList.getList().clear();
      }
    });
  }

  /**
   * Init draggable operation for column
   * 
   * @param draggableOptions
   */
  private void initDragOperation(DragAndDropColumn<?, ?> column) {

    // retrieve draggableOptions on the column
    DraggableOptions draggableOptions = column.getDraggableOptions();
    // use template to construct the helper. The content of the div will be set
    // after
    draggableOptions.setHelper($(Templates.INSTANCE.outerHelper().asString()));
    // opacity of the helper
    draggableOptions.setOpacity((float) 0.8);
    // cursor to use during the drag operation
    draggableOptions.setCursor(Cursor.MOVE);
    // set the revert option
    draggableOptions.setRevert(RevertOption.ON_INVALID_DROP);
    // prevents dragging when user click on the category drop-down list
    draggableOptions.setCancel("select");
  }

  /**
   * Add the columns to the table.
   * 
   * Use {@link DragAndDropColumn} instead of {@link Column}
   * @param sortHandler 
   */
  private void initTableColumns(final SelectionModel<ContactInfo> selectionModel, ListHandler<ContactInfo> sortHandler) {

    DragAndDropColumn<ContactInfo, Boolean> checkColumn = new DragAndDropColumn<ContactInfo, Boolean>(
        new CheckboxCell(true,true)) {
      @Override
      public Boolean getValue(ContactInfo object) {
        // Get the value from the selection model.
        return selectionModel.isSelected(object);
      }
    };
    checkColumn.setFieldUpdater(new FieldUpdater<ContactInfo, Boolean>() {
      public void update(int index, ContactInfo object, Boolean value) {
        // Called when the user clicks on a checkbox.
        selectionModel.setSelected(object, value);
      }
    });
    checkColumn.setCellDraggableOnly();
    initDragOperation(checkColumn);
    cellTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br>"));

    // First name.
    DragAndDropColumn<ContactInfo, String> firstNameColumn = new DragAndDropColumn<ContactInfo, String>(
        new EditTextCell()) {
      @Override
      public String getValue(ContactInfo object) {
        return object.getFirstName();
      }
    };
    firstNameColumn.setCellDraggableOnly();
    firstNameColumn.setSortable(true);
    sortHandler.setComparator(firstNameColumn, new Comparator<ContactInfo>() {
      public int compare(ContactInfo o1, ContactInfo o2) {
        return o1.getFirstName().compareTo(o2.getFirstName());
      }
    });

    
    initDragOperation(firstNameColumn);
    cellTable
        .addColumn(firstNameColumn, constants.cwCellTableColumnFirstName());
    firstNameColumn.setFieldUpdater(new FieldUpdater<ContactInfo, String>() {
      public void update(int index, ContactInfo object, String value) {
        // Called when the user changes the value.
        object.setFirstName(value);
        ContactDatabase.get().refreshDisplays();
      }
    });

    // Last name.
    DragAndDropColumn<ContactInfo, String> lastNameColumn = new DragAndDropColumn<ContactInfo, String>(
        new EditTextCell()) {
      @Override
      public String getValue(ContactInfo object) {
        return object.getLastName();
      }
    };
    lastNameColumn.setCellDraggableOnly();
    lastNameColumn.setSortable(true);
    sortHandler.setComparator(lastNameColumn, new Comparator<ContactInfo>() {
      public int compare(ContactInfo o1, ContactInfo o2) {
        return o1.getLastName().compareTo(o2.getLastName());
      }
    });

    
    initDragOperation(lastNameColumn);
    cellTable.addColumn(lastNameColumn, constants.cwCellTableColumnLastName());
    lastNameColumn.setFieldUpdater(new FieldUpdater<ContactInfo, String>() {
      public void update(int index, ContactInfo object, String value) {
        // Called when the user changes the value.
        object.setLastName(value);
        ContactDatabase.get().refreshDisplays();
      }
    });

    // Category.
    final Category[] categories = ContactDatabase.get().queryCategories();
    List<String> categoryNames = new ArrayList<String>();
    for (Category category : categories) {
      categoryNames.add(category.getDisplayName());
    }
    SelectionCell categoryCell = new SelectionCell(categoryNames);
    DragAndDropColumn<ContactInfo, String> categoryColumn = new DragAndDropColumn<ContactInfo, String>(
        categoryCell) {
      @Override
      public String getValue(ContactInfo object) {
        return object.getCategory().getDisplayName();
      }
    };
    categoryColumn.setCellDraggableOnly();
    initDragOperation(categoryColumn);
    cellTable.addColumn(categoryColumn, constants.cwCellTableColumnCategory());
    categoryColumn.setFieldUpdater(new FieldUpdater<ContactInfo, String>() {
      public void update(int index, ContactInfo object, String value) {
        for (Category category : categories) {
          if (category.getDisplayName().equals(value)) {
            object.setCategory(category);
          }
        }
        ContactDatabase.get().refreshDisplays();
      }
    });

    // Address.
    DragAndDropColumn<ContactInfo, String> addressColumn = new DragAndDropColumn<ContactInfo, String>(
        new TextCell()) {
      @Override
      public String getValue(ContactInfo object) {
        return object.getAddress();
      }
    };
    cellTable.addColumn(addressColumn, constants.cwCellTableColumnAddress());
    addressColumn.setCellDraggableOnly();
    initDragOperation(addressColumn);
  }

}

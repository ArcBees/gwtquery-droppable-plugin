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
package gwtquery.plugins.droppable.client.testoptionssample;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.draggable.client.Draggable.Draggable;
import static gwtquery.plugins.droppable.client.testoptionssample.TestOptionsSample.EVENT_BUS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.AxisOption;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.DraggableOptions.SnapMode;
import gwtquery.plugins.droppable.client.testoptionssample.ResetOptionEvent.ResetOptionEventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Panel displaying the options for a draggable
 * 
 * @author Julien Dramaix (julien.dramaix@gmail.com)
 * 
 */
public class DraggableOptionsPanel extends Composite implements
    ResetOptionEventHandler {

  @UiTemplate(value = "DraggableOptionsPanel.ui.xml")
  interface DraggableOptionsPanelUiBinder extends
      UiBinder<Widget, DraggableOptionsPanel> {
  }

  /**
   * Object defining a snap option (tolerance, mode, target)
   * 
   * @author Julien Dramaix (julien.dramaix@gmail.com)
   * 
   */
  private static class SnapChoice {

    private SnapMode mode;
    private String snapTarget;
    private int tolerance;

    public SnapChoice(int tolerance, SnapMode mode, String snapTarget) {
      this.tolerance = tolerance;
      this.mode = mode;
      this.snapTarget = snapTarget;
    }

    public SnapMode getMode() {
      return mode;
    }

    public String getSnapTarget() {
      return snapTarget;
    }

    public int getTolerance() {
      return tolerance;
    }
  }

  private static Map<String, Object> contaimentOptions;
  private static Map<String, CursorAt> cursorAtOptions;
  private static Map<String, SnapChoice> snapOptions;
  private static DraggableOptionsPanelUiBinder uiBinder = GWT
      .create(DraggableOptionsPanelUiBinder.class);

  static {
    cursorAtOptions = new HashMap<String, CursorAt>();
    cursorAtOptions.put("None", null);
    cursorAtOptions.put("at top left", new CursorAt(0, 0, null, null));
    cursorAtOptions.put("at top right", new CursorAt(0, null, null, 0));
    cursorAtOptions.put("at bottom left", new CursorAt(null, 0, 0, null));
    cursorAtOptions.put("at bottom right", new CursorAt(null, null, 0, 0));
    cursorAtOptions.put("at center", new CursorAt(50, 60, null, null));

    contaimentOptions = new HashMap<String, Object>();
    contaimentOptions.put("None", null);
    contaimentOptions.put("parent", "parent");
    contaimentOptions.put("demo box", ".demo");
    contaimentOptions.put("window", "window");

    snapOptions = new HashMap<String, SnapChoice>();
    snapOptions.put("None", null);
    snapOptions.put("Outher edges of the other draggable", new SnapChoice(50,
        SnapMode.OUTER, ".draggable"));
    snapOptions.put("Inner edges of main droppables", new SnapChoice(50,
        SnapMode.INNER, ".main-droppable"));
    snapOptions.put("Inner edges of second droppables", new SnapChoice(50,
        SnapMode.INNER, ".second-droppable"));

  }

  @UiField
  ListBox axisListBox;
  @UiField
  ListBox containmentListBox;
  @UiField
  ListBox cursorAtListBox;
  @UiField
  ListBox cursorListBox;
  @UiField
  TextBox delayBox;
  @UiField
  CheckBox disabledCheckBox;
  @UiField
  TextBox distanceBox;
  @UiField
  ListBox gridListBox;
  @UiField
  CheckBox handleCheckBox;
  @UiField
  ListBox helperListBox;
  @UiField
  TextBox opacityBox;
  @UiField
  TextBox revertDurationTextBox;
  @UiField
  ListBox revertListBox;
  @UiField
  CheckBox scrollCheckBox;
  @UiField
  TextBox scrollSensivityBox;
  @UiField
  TextBox scrollSpeedBox;
  @UiField
  ListBox snapListbox;
  
  private Element draggable;

  public DraggableOptionsPanel(Element draggable) {
    this.draggable = draggable;
    initWidget(uiBinder.createAndBindUi(this));
    EVENT_BUS.addHandler(ResetOptionEvent.TYPE, this);

    // use a scheduled command to ensure to init the object when the element is
    // draggable
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

      public void execute() {
        init();

      }
    });

  }

  @UiHandler(value = "axisListBox")
  public void onAxisChange(ChangeEvent e) {
    AxisOption axis = AxisOption.valueOf(axisListBox.getValue(axisListBox
        .getSelectedIndex()));
    getOptions().setAxis(axis);
  }

  @UiHandler(value = "containmentListBox")
  public void onContainmentChange(ChangeEvent e) {
    String containment = containmentListBox.getValue(containmentListBox
        .getSelectedIndex());
    Object realContainment = contaimentOptions.get(containment);
    if (realContainment instanceof String) {
      getOptions().setContainment((String) realContainment);
    } else {
      getOptions().setContainment((int[]) realContainment);
    }
  }

  @UiHandler(value = "cursorAtListBox")
  public void onCursorAtChange(ChangeEvent e) {
    String cursorAt = cursorAtListBox.getValue(cursorAtListBox
        .getSelectedIndex());

    getOptions().setCursorAt(cursorAtOptions.get(cursorAt));

  }

  @UiHandler(value = "cursorListBox")
  public void onCursorChange(ChangeEvent e) {
    Cursor c = Cursor.valueOf(cursorListBox.getValue(cursorListBox
        .getSelectedIndex()));
    getOptions().setCursor(c);

  }

  @UiHandler(value = "delayBox")
  public void onDelayChange(ValueChangeEvent<String> e) {
    getOptions().setDelay(new Integer(e.getValue()));
  }

  @UiHandler(value = "disabledCheckBox")
  public void onDisabledChange(ValueChangeEvent<Boolean> e) {
    getOptions().setDisabled(e.getValue());
  }

  @UiHandler(value = "distanceBox")
  public void onDistanceChange(ValueChangeEvent<String> e) {
    Integer distance;
    try {
      distance = new Integer(e.getValue());
    } catch (NumberFormatException ex) {
      Window.alert("Please specify a correct number for distance");
      return;
    }
    getOptions().setDistance(distance);
  }

  @UiHandler(value = "gridListBox")
  public void onGridChange(ChangeEvent e) {
    String grid = gridListBox.getValue(gridListBox.getSelectedIndex());
    if ("None".equals(grid)) {
      getOptions().setGrid(null);
    } else {
      String[] dimension = grid.split(",");
      getOptions().setGrid(
          new int[] { new Integer(dimension[0]), new Integer(dimension[1]) });
    }
  }

  @UiHandler(value = "helperListBox")
  public void onHelperChange(ChangeEvent e) {
    HelperType type = HelperType.valueOf(helperListBox.getValue(helperListBox
        .getSelectedIndex()));

    if (type == HelperType.ELEMENT) {
      GQuery helper = $("<div class=\"draggable\" style=\"background-color:#B5D5FF; border:1px dotted #1C4EBC; \">I'm a custom helper</div>");
      getOptions().setHelper(helper);
    } else {
      getOptions().setHelper(type);
    }
  }

  @UiHandler(value = "handleCheckBox")
  public void onMultiSelectChange(ValueChangeEvent<Boolean> e) {
    if (e.getValue()) {
      getOptions().setHandle("#handle");
    } else {
      getOptions().setHandle(null);
    }
  }

  @UiHandler(value = "opacityBox")
  public void onOpacityChange(ValueChangeEvent<String> e) {
    String opacityString = e.getValue();

    Float opacity;
    if (opacityString == null || opacityString.length() == 0) {
      opacity = null;
    } else {
      try {
        opacity = new Float(e.getValue());
      } catch (NumberFormatException ex) {
        Window.alert("Please specify a correct number for opacity");
        return;
      }
    }
    if (opacity != null && opacity > 1) {
      Window.alert("Opacity must be below than 1.");
      return;
    }
    getOptions().setOpacity(opacity);
  }

  public void onResetOption(ResetOptionEvent event) {
    if (event.getOptionsPanel() == this) {
      $(draggable).as(Draggable).options(new DraggableOptions());
      init();
    }

  }

  @UiHandler(value = "revertListBox")
  public void onRevertChange(ChangeEvent e) {
    String revert = revertListBox.getValue(revertListBox.getSelectedIndex());
    getOptions().setRevert(RevertOption.valueOf(revert));
  }

  @UiHandler(value = "revertDurationTextBox")
  public void onRevertDurationChange(ValueChangeEvent<String> e) {
    String revertDuration = e.getValue();
    Integer revertDurationInt;
    if (revertDuration == null || revertDuration.length() == 0) {
      revertDurationInt = null;
    } else {
      try {
        revertDurationInt = new Integer(e.getValue());
      } catch (NumberFormatException ex) {
        Window.alert("Please specify a correct number for the revert duration");
        return;
      }
    }
    getOptions().setRevertDuration(revertDurationInt);
  }

  @UiHandler(value = "scrollCheckBox")
  public void onScrollChange(ValueChangeEvent<Boolean> e) {
    boolean scroll = e.getValue();
    getOptions().setScroll(scroll);
    scrollSensivityBox.setEnabled(scroll);
    scrollSpeedBox.setEnabled(scroll);

  }

  @UiHandler(value = "scrollSensivityBox")
  public void onScrollSensitivityChange(ValueChangeEvent<String> e) {
    Integer scrollSensitivity;
    try {
      scrollSensitivity = new Integer(e.getValue());
    } catch (NumberFormatException ex) {
      Window.alert("Please specify a correct number for scrollSensitivity");
      return;
    }
    getOptions().setScrollSensitivity(scrollSensitivity);
  }

  @UiHandler(value = "scrollSpeedBox")
  public void onScrollSpeedChange(ValueChangeEvent<String> e) {
    Integer scrollSpeed;
    try {
      scrollSpeed = new Integer(e.getValue());
    } catch (NumberFormatException ex) {
      Window.alert("Please specify a correct number for scrollSpeed");
      return;
    }
    getOptions().setScrollSpeed(scrollSpeed);
  }

  @UiHandler(value = "snapListbox")
  public void onSnapListBoxChange(ChangeEvent e) {
    String snapOption = snapListbox.getValue(snapListbox.getSelectedIndex());
    SnapChoice snapChoice = snapOptions.get(snapOption);
    if (snapChoice == null) {
      getOptions().setSnap((String) null);
    } else {
      getOptions().setSnap(snapChoice.getSnapTarget());
      getOptions().setSnapTolerance(snapChoice.getTolerance());
      getOptions().setSnapMode(snapChoice.getMode());
    }

  }

  private DraggableOptions getOptions() {
    return $(draggable).as(Draggable).options();
  }

  private void init() {
    DraggableOptions options = getOptions();

    delayBox.setValue("" + options.getDelay(), false);
    distanceBox.setValue("" + options.getDistance(), false);
    revertDurationTextBox.setValue("" + options.getRevertDuration());
    scrollSensivityBox.setValue("" + options.getScrollSensitivity());
    scrollSpeedBox.setValue("" + options.getScrollSpeed());
    if (options.getOpacity() != null) {
      opacityBox.setValue("" + options.getOpacity());
    }

    scrollCheckBox.setValue(options.isScroll());
    disabledCheckBox.setValue(options.isDisabled(), false);
    handleCheckBox.setValue(false, false);

    initListBox(HelperType.values(), helperListBox, options.getHelperType());
    initListBox(Cursor.values(), cursorListBox, options.getCursor());
    initListBox(AxisOption.values(), axisListBox, options.getAxis());
    initListBox(RevertOption.values(), revertListBox, options.getRevert());
    initListBox(contaimentOptions, containmentListBox);
    initListBox(cursorAtOptions, cursorAtListBox);
    initListBox(snapOptions, snapListbox);

    gridListBox.addItem("None", "None");
    gridListBox.addItem("snap draggable to a 20x20 grid", "20,20");
    gridListBox.addItem("snap draggable to a 40x40 grid", "40,40");
    gridListBox.addItem("snap draggable to a 80x80 grid", "80,80");
    gridListBox.addItem("snap draggable to a 100x100 grid", "100,100");
    gridListBox.setSelectedIndex(0);

  }

  private void initListBox(Enum<?>[] values, ListBox listBox,
      Enum<?> defaultValue) {
    listBox.clear();
    int i = 0;
    for (Enum<?> e : values) {
      listBox.addItem(e.name(), e.name());
      if (e == defaultValue) {
        listBox.setSelectedIndex(i);
      }
      i++;
    }
  }

  private void initListBox(Map<String, ?> choices, ListBox listBox) {
    listBox.clear();
    int i = 0;
    for (String s : choices.keySet()) {
      listBox.addItem(s);
      if (s.equals("None")) {
        listBox.setSelectedIndex(i);
      }
      i++;
    }
  }

}
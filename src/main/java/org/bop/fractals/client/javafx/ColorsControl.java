/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bop.fractals.client.javafx;

import java.util.stream.Stream;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Marco Ruiz
 * @since Mar 11, 2017
 */
public class ColorsControl {

    private final ToggleGroup group = new ToggleGroup();
    private Text indicator;
    private double layoutX, layoutY;
    private double width;
	private Pane parent;

	public ColorsControl(Pane parent, double layoutX, double layoutY, double btnWidth) {
		this(parent, layoutX, layoutY, btnWidth, "Black", "Magenta", "Blue", "Cyan", "Green", "Yellow", "Orange", "Red", "White");
	}

	public ColorsControl(Pane parent, double layoutX, double layoutY, double btnWidth, String... colors) {
		this.parent = parent;
		this.layoutX = layoutX;
		this.layoutY = layoutY;
		this.width = btnWidth;

		addTextIndicator();
        group.selectedToggleProperty().addListener((obsVal, oldToggle, newToggle) -> indicator.setFill((Color)newToggle.getUserData()));
		Stream.of(colors).forEach(this::addColor);
	}

	private void addTextIndicator() {
		indicator = new Text(layoutX, layoutY * 2, "Color:");
		indicator.setFont(Font.font("Arial Narrow Bold", 20));
		this.parent.getChildren().add(indicator);

		this.layoutX += 50;
	}

	public void addColor(String colorStr) {
		ToggleButton colorBtn = new ToggleButton();
		colorBtn.setUserData(Color.valueOf(colorStr));

		colorBtn.setSelected(group.getToggles().isEmpty());
		colorBtn.setToggleGroup(group);

		colorBtn.setLayoutX(layoutX + (width + 1) * group.getToggles().size());
		colorBtn.setLayoutY(layoutY);
		colorBtn.setPrefWidth(width);
		colorBtn.setStyle("-fx-base: " + colorStr);

		parent.getChildren().add(colorBtn);
	}

	public Color getSelected() {
		return (Color) group.getSelectedToggle().getUserData();
	}
}



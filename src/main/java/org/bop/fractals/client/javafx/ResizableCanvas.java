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

import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * @author Marco Ruiz
 * @since Mar 11, 2017
 */
public class ResizableCanvas extends Canvas {

	private Runnable onResize;
	private Pane parent;

	public ResizableCanvas(HBox grandParent, double width, double height, Runnable onResizeListener) {
		super(width, height);
		onResize = onResizeListener;
		parent = new Pane();
		parent.setPrefSize(width, height);
		HBox.setHgrow(parent, Priority.ALWAYS);
		parent.getChildren().add(this);

		if (!grandParent.getChildren().isEmpty())
			grandParent.getChildren().add(new Separator(Orientation.VERTICAL));
		grandParent.getChildren().add(parent);
	}

	@Override
    public double prefWidth(double height) {
        return parent.getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return parent.getHeight();
    }

	public Pane getParentPane() {
		return parent;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public void resize(double width, double height) {
		super.setWidth(width);
		super.setHeight(height);
		onResize.run();
	}
}

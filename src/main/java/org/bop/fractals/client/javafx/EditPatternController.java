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

import java.util.ArrayList;
import java.util.List;

import org.bop.fractals.GeometricPatternFractalGenerator;
import org.bop.fractals.line.FractalLine;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * @author Marco Ruiz
 * @since Mar 8, 2017
 */
public class EditPatternController {

    @FXML private Pane controlPane;
	private ColorsControl colorsControl;
	@FXML private Slider recursions;

	@FXML private Slider cellSizeSlider;
	@FXML private CheckBox gridEnabled;

    @FXML private SplitPane drawPane;
    private ResizableCanvas patternCanvas;
    private ResizableCanvas fractalCanvas;

	private List<FractalLine> pattern = new ArrayList<>();
	private Point[] editPoints = new Point[2];

    private GeometricPatternFractalGenerator<FractalLine> fractalGenerator;

    public void initialize() {
    	// Custom colors control
    	colorsControl = new ColorsControl(controlPane, 98, 21, 20,
    			"Black", "Brown", "Gray", "Magenta", "Blue", "Cyan", "Green", "Yellow", "Orange", "Red");

    	cellSizeSlider.valueProperty().addListener(ev -> resetGrid());

    	// Canvases to draw the pattern and the fractal
    	patternCanvas = new ResizableCanvas(drawPane, 500, 750, () -> patternCanvas.drawLines(pattern));
    	fractalCanvas = new ResizableCanvas(drawPane, 500, 750, () -> generateFractal());

		// Mouse listeners to define patterns
    	patternCanvas.setOnMousePressed(ev -> setPoint(ev, 0));
    	patternCanvas.setOnMouseDragged(ev -> setPoint(ev, 1));
    	patternCanvas.setOnMouseReleased(ev -> recordLine(ev));
    }

	private void setPoint(MouseEvent event, int pointIndex) {
    	editPoints[pointIndex] = new Point(event, patternCanvas.getGridCellSide());

    	patternCanvas.drawLines(pattern);
    	if (pointIndex == 1)
    		patternCanvas.drawLine(editPoints[0].x, editPoints[0].y, editPoints[1].x, editPoints[1].y, colorsControl.getSelected());
    }

    private void recordLine(MouseEvent event) {
    	editPoints[1] = new Point(event, patternCanvas.getGridCellSide());
    	pattern.add(new FractalLine(editPoints[0].x, editPoints[0].y, editPoints[1].x, editPoints[1].y, colorsControl.getSelected()));
    	patternCanvas.drawLines(pattern);
		generateFractal();
    }

	public void reset() {
		pattern.clear();
		fractalGenerator = null;
		fractalCanvas.clearCanvas();
		patternCanvas.clearCanvas();
	}

	public void resetGrid() {
		patternCanvas.setGridCellSide(gridEnabled.isSelected() ? (int) cellSizeSlider.getValue() : 0);
		patternCanvas.redraw();
	}

	public void generateFractal() {
		if (pattern.isEmpty()) return;
		fractalGenerator = new GeometricPatternFractalGenerator<FractalLine>(pattern, (int)recursions.getValue()) {
			protected void addFractalShape(FractalLine line) {
				super.addFractalShape(line);
				fractalCanvas.drawLine(line.Ax, line.Ay, line.Bx, line.By, line.color);
			}
		};
		fractalCanvas.clearCanvas();
		fractalCanvas.drawLines(pattern);
		fractalGenerator.generateFractalSync();
	}

	//====================
	// POINT HELPER CLASS
	//====================
	class Point {
		final float x, y;

		public Point(MouseEvent event) {
			this((float)event.getX(), (float)event.getY(), 0);
		}

		public Point(MouseEvent event, int cellSide) {
			this((float)event.getX(), (float)event.getY(), cellSide);
		}

		public Point(float x, float y, int cellSide) {
			this.x = getClosest(x, cellSide);
			this.y = getClosest(y, cellSide);
		}

		private float getClosest(float coord, int cellSide) {
			return (cellSide == 0) ? coord : Math.round(coord / cellSide) * cellSide;
		}
	}
}


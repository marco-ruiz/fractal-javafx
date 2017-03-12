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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * @author Marco Ruiz
 * @since Mar 8, 2017
 */
public class EditPatternController {

    @FXML private Pane controlPane;
	private ColorsControl colorsControl;
	@FXML private Slider recursions;

    @FXML private HBox drawPane;
    @FXML private Canvas patternCanvas;
    @FXML private Canvas fractalCanvas;

	private List<FractalLine> pattern = new ArrayList<>();
	private Point[] editPoints = new Point[2];

    private GeometricPatternFractalGenerator<FractalLine> fractalGenerator;

    public void initialize() {
    	// Custom colors control
    	colorsControl = new ColorsControl(controlPane, 98, 21, 20,
    			"Black", "Brown", "Gray", "Magenta", "Blue", "Cyan", "Green", "Yellow", "Orange", "Red");

    	// Canvases to draw the pattern and the fractal
    	patternCanvas = new ResizableCanvas(drawPane, 500, 750, () -> drawLines(patternCanvas, pattern));
    	fractalCanvas = new ResizableCanvas(drawPane, 500, 750, () -> generateFractal());

		// Mouse listeners to define patterns
    	patternCanvas.setOnMousePressed(ev -> setPoint(ev, 0));
    	patternCanvas.setOnMouseDragged(ev -> setPoint(ev, 1));
    	patternCanvas.setOnMouseReleased(ev -> recordLine(ev));
    }

	private void setPoint(MouseEvent event, int pointIndex) {
    	editPoints[pointIndex] = new Point(event);

    	GraphicsContext gc = patternCanvas.getGraphicsContext2D();
    	drawLines(patternCanvas, pattern);
    	if (pointIndex == 1)
    		drawLine(gc, editPoints[0].x, editPoints[0].y, editPoints[1].x, editPoints[1].y, colorsControl.getSelected());
    }

    private void recordLine(MouseEvent event) {
    	editPoints[1] = new Point(event);
    	pattern.add(new FractalLine(editPoints[0].x, editPoints[0].y, editPoints[1].x, editPoints[1].y, colorsControl.getSelected()));
    	drawLines(patternCanvas, pattern);
		generateFractal();
    }

	private void drawLines(Canvas canvas, List<FractalLine> lines) {
    	clearCanvas(canvas);
    	GraphicsContext gc = canvas.getGraphicsContext2D();
		lines.stream().forEach(line -> drawLine(gc, line.Ax, line.Ay, line.Bx, line.By, line.color));
	}

	private void drawLine(GraphicsContext gc, float ax, float ay, float bx, float by, Object color) {
		gc.setStroke((Color) color);
		gc.strokeLine(ax, ay, bx, by);
	}

	private void clearCanvas(Canvas canvas) {
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void reset() {
		pattern.clear();
		fractalGenerator = null;
		clearCanvas(fractalCanvas);
		clearCanvas(patternCanvas);
	}

	public void generateFractal() {
		if (pattern.isEmpty()) return;
		fractalGenerator = new GeometricPatternFractalGenerator<FractalLine>(pattern, (int)recursions.getValue()) {
			protected void addFractalShape(FractalLine line) {
				super.addFractalShape(line);
				drawLine(fractalCanvas.getGraphicsContext2D(), line.Ax, line.Ay, line.Bx, line.By, line.color);
			}
		};
		clearCanvas(fractalCanvas);
		drawLines(fractalCanvas, pattern);
		fractalGenerator.generateFractalSync();
	}

	//====================
	// POINT HELPER CLASS
	//====================
	class Point {
		final float x, y;

		Point(MouseEvent event) {
			this((float)event.getX(), (float)event.getY());
		}

		Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
}


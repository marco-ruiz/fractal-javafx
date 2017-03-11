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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bop.fractals.GeometricPatternFractalGenerator;
import org.bop.fractals.line.FractalLine;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * @author Marco Ruiz
 * @since Mar 8, 2017
 */
public class EditPatternController {

    private static Point getPoint(MouseEvent event) {
		return new Point((float)event.getX(), (float)event.getY());
	}

    @FXML private Pane appPane;
    @FXML private Pane controlPane;
    @FXML private Button modeBtn;
    @FXML private Slider recursions;
    @FXML private ColorPicker colorPicker;
    @FXML private Canvas canvas;

	private List<FractalLine> pattern = new ArrayList<>();
	private Point[] editPoints = new Point[2];

    private GeometricPatternFractalGenerator<FractalLine> fractalGenerator;

    public void initialize() {
    	colorPicker.setValue(Color.BLACK);
    	canvas.setOnMousePressed(ev -> setPoint(ev, 0));
    	canvas.setOnMouseDragged(ev -> setPoint(ev, 1));
    	canvas.setOnMouseReleased(ev -> recordLine(ev));
    }

    private void setPoint(MouseEvent event, int pointIndex) {
    	editPoints[pointIndex] = getPoint(event);

    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	drawLines(pattern);
    	if (pointIndex == 1)
    		drawLine(gc, editPoints[0].x, editPoints[0].y, editPoints[1].x, editPoints[1].y, colorPicker.getValue());
    }

	private void drawLines(List<FractalLine> lines) {
    	clearCanvas();
    	GraphicsContext gc = canvas.getGraphicsContext2D();
		lines.stream().forEach(line -> drawLine(gc, line.Ax, line.Ay, line.Bx, line.By, line.rgbColorValue));
	}

	private void drawLine(GraphicsContext gc, float ax, float ay, float bx, float by, Object color) {
		gc.setStroke((Color) color);
		gc.strokeLine(ax, ay, bx, by);
	}

	private void clearCanvas() {
		canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

    private void recordLine(MouseEvent event) {
    	editPoints[1] = getPoint(event);
    	pattern.add(new FractalLine(editPoints[0].x, editPoints[0].y, editPoints[1].x, editPoints[1].y, colorPicker.getValue()));
    	drawLines(pattern);
    }

	public void reset() {
		pattern.clear();
		fractalGenerator = null;
		switchMode(false);
	}

	public void generateFractal() {
		appPane.setDisable(true);
		fractalGenerator = new GeometricPatternFractalGenerator<FractalLine>(pattern, (int)recursions.getValue()) {
			protected void addFractalShape(FractalLine line) {
				super.addFractalShape(line);
				drawLine(canvas.getGraphicsContext2D(), line.Ax, line.Ay, line.Bx, line.By, line.rgbColorValue);
			}
		};
		fractalGenerator.generateFractalSync();
		switchMode(true);
		appPane.setDisable(false);
	}

	public void switchMode() {
		switchMode(!canvas.isDisabled());
	}

	private void switchMode(boolean toViewMode) {
		canvas.setDisable(toViewMode);
		modeBtn.setText(toViewMode ? "Edit Pattern" : "Show Fractal");

		if (toViewMode && fractalGenerator == null) {
			clearCanvas();
			return;
		}

		List<FractalLine> output = new ArrayList<>();
		output.addAll(pattern);
		if (toViewMode)
			output.addAll(fractalGenerator.getFractal());

    	drawLines(output);
	}
}

class Point {
	final float x, y;

	Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
}


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

import java.util.List;
import java.util.stream.IntStream;

import org.bop.fractals.line.FractalLine;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * @author Marco Ruiz
 * @since Mar 11, 2017
 */
public class ResizableCanvas extends Canvas {

	private Runnable onResize;
	private Pane parent;
	private int gridCellSide = 0;

	private Scroller scroller = new Scroller();

	public ResizableCanvas(SplitPane grandParent, double width, double height, Runnable onResizeListener) {
		super(width, height);
		onResize = onResizeListener;
		parent = new Pane();
		parent.setPrefSize(width, height);
		HBox.setHgrow(parent, Priority.ALWAYS);
		parent.getChildren().add(this);

		grandParent.getItems().add(parent);
		drawGrid();

//		setOnScroll(scroller::scroll);
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
		redraw();
	}

	public void redraw() {
		onResize.run();
	}

	public int getGridCellSide() {
		return gridCellSide;
	}

	public void setGridCellSide(int gridCellSide) {
		this.gridCellSide = gridCellSide;
		drawGrid();
	}


	//=================
	// CLEAR CANVAS
	//=================
	public GraphicsContext clearCanvas() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
		drawGrid();
		return gc;
	}

	//=================
	// DRAWING GRID
	//=================
	public void drawGrid() {
		if (gridCellSide == 0) return;
		GraphicsContext gc = this.getGraphicsContext2D();

		gc.setLineDashes(2, 5);
		gc.setLineWidth(0.25);

		getGridStream(getWidth()).forEach(x -> drawLine(gc, x, 0, x, (float) getHeight(), Color.BLACK));
		getGridStream(getHeight()).forEach(y -> drawLine(gc, 0, y, (float) getWidth(), y, Color.BLACK));

		gc.setLineWidth(1);
		gc.setLineDashes();
	}

	private IntStream getGridStream(double dimSize) {
		return IntStream.rangeClosed(0, (int) dimSize / gridCellSide).map(index -> index * gridCellSide);
	}

	//================
	// DRAWING LINES
	//================
	public void drawLines(List<FractalLine> lines) {
    	GraphicsContext gc = clearCanvas();
		lines.stream().forEach(line -> drawLine(gc, line));
	}

	public void drawLine(FractalLine line) {
		drawLine(getGraphicsContext2D(), line);
	}

	public void drawLine(GraphicsContext gc, FractalLine line) {
		drawLine(gc,
					scroller.translate(line.Ax, 0),
					scroller.translate(line.Ay, 1),
					scroller.translate(line.Bx, 0),
					scroller.translate(line.By, 1),
					line.color);
	}

	public void drawLine(double ax, double ay, double bx, double by, Object color) {
    	drawLine(getGraphicsContext2D(), ax, ay, bx, by, color);
	}

	private void drawLine(GraphicsContext gc, double ax, double ay, double bx, double by, Object color) {
		gc.setStroke((Color) color);
		gc.strokeLine(ax, ay, bx, by);
	}

	//==============
	// ZOOM HELPER
	//==============
	class Scroller {
		private double scroll = 0;

		// computed
		private double offset[] = {0, 0};
		private double zoom = 1;

		public void scroll(ScrollEvent event) {
			scroll += event.getDeltaY();

			offset[0] = translate(event.getX(), 0);
			offset[1] = translate(event.getY(), 1);

			zoom = computeZoom(scroll);
			offset[0] = offset[0] - (offset[0] * zoom);
			offset[1] = offset[1] - (offset[1] * zoom);

			redraw();
		}

		/**
		 * Returns the canvas coordinate corresponding to 'value'
		 *
		 * @param value
		 * @param translateIndex
		 * @return
		 */
		private double translate(double value, int translateIndex) {
			return value * zoom + offset[translateIndex];
		}

		private double computeZoom(double value) {
	    	return (value > 0) ? value + 1 : 1 / (1 - value);
		}
	}
}


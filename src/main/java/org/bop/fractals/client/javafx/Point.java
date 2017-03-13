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

import javafx.scene.input.MouseEvent;

/**
 * @author Marco Ruiz
 * @since Mar 13, 2017
 */
public class Point {
	public final float x, y;

	public Point(MouseEvent event) {
		this((float) event.getX(), (float) event.getY(), 0);
	}

	public Point(MouseEvent event, int cellSide) {
		this((float) event.getX(), (float) event.getY(), cellSide);
	}

	public Point(float x, float y) {
		this(x, y, 0);
	}

	public Point(float x, float y, int cellSide) {
		this.x = getClosest(x, cellSide);
		this.y = getClosest(y, cellSide);
	}

/*
	public Point(float x, float y, int cellSide) {
		this.x = getClosest(x, cellSide) / getZoomValue();
		this.y = getClosest(y, cellSide) / getZoomValue();
	}
*/

	private float getClosest(float coord, int cellSide) {
		return (cellSide == 0) ? coord : Math.round(coord / cellSide) * cellSide;
	}
}



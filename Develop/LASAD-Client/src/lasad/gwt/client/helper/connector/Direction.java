/*
 * Copyright 2007 Michał Baliński
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

package lasad.gwt.client.helper.connector;

/**
 * Enumeration representing directions.
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public class Direction {

	public static final Direction UP = new Direction("UP", 0.0f);
	public static final Direction DOWN = new Direction("DOWN", 180.0f);
	public static final Direction LEFT = new Direction("LEFT", 270.0f);
	public static final Direction RIGHT = new Direction("RIGHT", 90.0f);

	private final String id;
	private final float angle;

	private Direction(String id, float angle) {
		this.id = id;
		this.angle = angle;
	}

	/**
	 * @return all defined directions
	 */
	public static Direction[] getAll() {
		return new Direction[] { UP, DOWN, LEFT, RIGHT };
	}

	/**
	 * @return true if it is horizontal direction
	 */
	public boolean isHorizontal() {
		return this == LEFT || this == RIGHT;
	}

	/**
	 * @return true if it is vertical direction
	 */
	public boolean isVertical() {
		return this == UP || this == DOWN;
	}

	/**
	 * @return representing angle value
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return id;
	}

	public static Direction[] computeDirections(Connector c1, Connector c2) {
		Direction[] d1 = c1.getDirections();
		Direction[] d2 = c2.getDirections();
		
		Direction[] bestDirections = new Direction[2];
		double best = Double.MAX_VALUE;
		for (int i = 0; i < d1.length; i++) {
			for (int j = 0; j < d2.length; j++) {
				double actual = c1.getConnectionPoint(d1[i]).distance(c2.getConnectionPoint(d2[j]));
				if (actual < best) {
					best = actual;
					bestDirections[0] = d1[i];
					bestDirections[1] = d2[j];
				}
			}
		}
		return bestDirections;
	}
}

package com.wyrli.spermsizer.measurements;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BreadthFirstSearch {

	/** Returns the shortest path (constrained to a collection of valid points) between two points. */
	public static List<Point> getShortestPath(Collection<Point> valid, Point start, Point end) {
		List<Point> points = new ArrayList<Point>();

		if (start.equals(end)) {
			points.add(start);
			points.add(end);
			return points;
		}

		Queue<Node> queue = new ArrayDeque<Node>();
		queue.add(new Node(end, null));

		Set<Point> visited = new HashSet<Point>();
		visited.add(end);

		Node destination = null;

		while (!queue.isEmpty()) {
			Node node = queue.remove();

			if (node.point.equals(start)) {
				destination = node;
				break;
			}

			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					// Ignore self.
					if (x == 0 && y == 0) {
						continue;
					}

					// Create neighboring point.
					int xNew = node.point.x + x;
					int yNew = node.point.y + y;
					Point pointNew = new Point(xNew, yNew);

					// Ignore points that have already been visited.
					if (visited.contains(pointNew)) {
						continue;
					}

					// Ignore invalid points.
					if (!valid.contains(pointNew)) {
						continue;
					}

					// Prevent point from being revisited.
					visited.add(pointNew);

					// Add neighboring point to queue.
					queue.add(new Node(pointNew, node));
				}
			}
		}

		// Backtrack to build the path from start to end.
		while (destination != null) {
			points.add(destination.point);
			destination = destination.parent;
		}

		return points;
	}

	private static class Node {
		private final Point point;
		private final Node parent;

		private Node(Point point, Node parent) {
			this.point = point;
			this.parent = parent;
		}
	}
}
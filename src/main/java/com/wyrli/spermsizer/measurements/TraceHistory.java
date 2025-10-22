package com.wyrli.spermsizer.measurements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wyrli.spermsizer.config.Settings;

public class TraceHistory {
	public static final Map<String, TraceHistory> ALL = new LinkedHashMap<String, TraceHistory>();

	private List<Marker> markers = new ArrayList<Marker>();
	private List<Marker> markersRedo = new ArrayList<Marker>();

	private List<TraceResult> results = new ArrayList<TraceResult>();
	private List<TraceResult> resultsRedo = new ArrayList<TraceResult>();

	public List<Marker> addMarker(Marker marker) {
		// Any markers that have been undone will be removed.
		markersRedo.clear();
		markers.add(marker);

		// Any results that have been undone will be removed.
		// Since results may still be processing, their tasks are also cancelled.
		for (TraceResult result : resultsRedo) {
			result.cancelTask();
		}
		resultsRedo.clear();

		// Check if there are enough markers to generate a result.
		if (markers.size() % Settings.markers != 0) {
			return null;
		}

		// Get all of the pending markers.
		List<Marker> pending = new ArrayList<Marker>();
		int count = Settings.markers;
		while (count > 0) {
			pending.add(markers.get(markers.size() - count));
			count--;
		}

		return pending;
	}

	public void addResult(TraceResult result) {
		results.add(result);
	}

	public int getMarkerNumber() {
		return markers.size() % Settings.markers;
	}

	public Marker undo() {
		boolean undoResult = markers.size() % Settings.markers == 0;
		Marker undone = transfer(markers, markersRedo);

		if (undone != null) {
			if (undoResult) {
				transfer(results, resultsRedo);
			}
			return undone;
		}

		return null;
	}

	public Marker redo() {
		Marker redid = transfer(markersRedo, markers);

		if (redid != null) {
			if (markers.size() % Settings.markers == 0) {
				transfer(resultsRedo, results);
			}
			return redid;
		}

		return null;
	}

	public boolean canUndo() {
		return !markers.isEmpty();
	}

	public boolean canRedo() {
		return !markersRedo.isEmpty();
	}

	public Set<Marker> getMarkers() {
		return new HashSet<Marker>(markers);
	}

	public List<TraceResult> getResults() {
		return new ArrayList<TraceResult>(results);
	}

	public static <T> T transfer(List<T> from, List<T> to) {
		if (from.isEmpty()) {
			return null;
		}

		T obj = from.remove(from.size() - 1);
		to.add(obj);

		return obj;
	}

	public static TraceHistory get(String id) {
		TraceHistory history = ALL.get(id);

		if (history == null) {
			history = new TraceHistory();
			ALL.put(id, history);
		}

		return history;
	}
}
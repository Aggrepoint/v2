package com.aggrepoint.adk.taglib.table;

import java.util.ArrayList;
import java.util.HashMap;

public class Row {
	private int idx;
	private ArrayList<String> arrCols = new ArrayList<String>();

	public Row(int idx) {
		this.idx = idx;
	}

	public void addCol(String col) {
		arrCols.add(col);
	}

	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idx", idx);
		map.put("cols", arrCols);
		return map;
	}
}

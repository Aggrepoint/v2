package com.aggrepoint.adk.taglib.table;

public class TableRowInfo {
	TableTag table;
	Object obj;

	public TableRowInfo(TableTag table, Object obj) {
		this.table = table;
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	public int getIdx() {
		return table.idx;
	}

	public boolean isFirst() {
		return table.idx == 0;
	}

	public boolean isLast() {
		return !table.iterator.hasNext();
	}

	public int getCount() {
		return table.colList.size();
	}

	public int getTotalCount() {
		return table.iTotalCount;
	}

	public int getPageSize() {
		return table.iPageSize;
	}

	public int getPages() {
		return table.iPageCount;
	}

	public int getPageNo() {
		return table.iPageNo;
	}

}

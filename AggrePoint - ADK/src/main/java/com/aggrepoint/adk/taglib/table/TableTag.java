package com.aggrepoint.adk.taglib.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.icebean.core.adb.ADBList;
import com.icebean.core.common.StringUtils;
import com.icebean.core.msg.MessageBoundle;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * 用于生成表格
 * 
 * @author YJM
 */
public class TableTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	protected String strName;
	/** 列表对象 */
	protected Collection<?> colList;
	/** 符合条件的数据对象总数 */
	protected int iTotalCount;
	/** 每页的数据对象数 */
	protected int iPageSize;
	/** 总页数 */
	protected int iPageCount;
	/** 当前页号 */
	protected int iPageNo;
	/** 当前排序方法 */
	protected String strSort;
	/** 总记录数，当前页数等信息。(0)为总记录数，(1)为总页数，(2)为每页记录数 */
	protected String strSummary = "";
	protected String strAttrs = "";

	protected ArrayList<HashMap<String, Object>> columns;
	protected Vector<Row> vecRows;
	protected Iterator<?> iterator;
	protected int idx;
	protected Object rowObject;
	protected Row row;

	public void setName(String str) {
		strName = str;
	}

	public void setList(Object list) throws JspException {
		if (list instanceof ADBList) {
			ADBList<?> adbl = (ADBList<?>) list;
			colList = adbl.m_vecObjects;
			iTotalCount = adbl.getTotalCount();
			iPageSize = adbl.getItemsPerPage();
			iPageCount = adbl.getPageCount();
			iPageNo = adbl.getPageNo();
		} else if (list instanceof Collection)
			colList = (Collection<?>) list;
		else
			throw new JspException(
					"List attribute of table tag should be in type of either Collection or ADBList.");
	}

	public void setTotalcount(int c) {
		iTotalCount = c;
	}

	public void setPagesize(int i) {
		iPageSize = i;
	}

	public void setPagecount(int i) {
		iPageCount = i;
	}

	public void setPageno(int i) {
		iPageNo = i;
	}

	public void setSort(String sort) {
		strSort = sort;
	}

	public void setSummary(String str) {
		if (str != null)
			strSummary = str;
	}

	public String getSummary() {
		return MessageBoundle.constructMessageStatic(strSummary, new String[] {
				Integer.toString(iTotalCount), Integer.toString(iPageCount),
				Integer.toString(iPageSize) });
	}

	@Override
	public void setDynamicAttribute(String arg0, String arg1, Object arg2)
			throws JspException {
		if (arg1 == null || arg2 == null)
			return;

		strAttrs += " " + arg1 + "='" + StringUtils.toHTML(arg2.toString())
				+ "'";
	}

	public void addColumn(TableColumnTag col) {
		try {
			if (idx == 1) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("title", col.strTitle == null ? "" : col.strTitle);
				map.put("sortasc", col.strSortAsc == null ? "" : col.strSortAsc);
				map.put("sortdesc", col.strSortDesc == null ? "" : col.strSortDesc);
				map.put("attrs", col.strAttrs == null ? "" : col.strAttrs);
			}

			row.addCol(col.getContent(rowObject));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int doStartTag() throws JspException {
		if (colList == null || colList.size() == 0)
			return SKIP_BODY;

		columns = new ArrayList<HashMap<String, Object>>();
		vecRows = new Vector<Row>();

		idx = 1;
		if (colList != null) {
			iterator = colList.iterator();
			rowObject = iterator.next();
			pageContext.setAttribute("row", new TableRowInfo(this, rowObject));
			row = new Row(idx);
			vecRows.add(row);
		}

		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() {
		try {
			if (iterator != null && iterator.hasNext()) {
				// 下一行
				idx++;
				rowObject = iterator.next();
				pageContext.setAttribute("row", new TableRowInfo(this,
						rowObject));
				row = new Row(idx);
				vecRows.add(row);

				return EVAL_BODY_BUFFERED;
			} else
				return SKIP_BODY;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SKIP_BODY;
	}

	protected HashMap<String, Object> getTemplateRoot() {
		HashMap<String, Object> root = new HashMap<String, Object>();

		// { table
		HashMap<String, Object> tableMap = new HashMap<String, Object>();
		root.put("table", tableMap);

		tableMap.put("attrs", strAttrs);
		tableMap.put("summary", getSummary());
		tableMap.put("pageno", iPageNo);
		tableMap.put("pagecount", iPageCount);

		int st = iPageNo - iPageSize / 2;
		if (st + iPageSize - 1 > iPageCount)
			st = iPageCount - iPageSize + 1;
		if (st < 1)
			st = 1;
		int ed = st + iPageSize - 1;
		if (ed > iPageCount)
			ed = iPageCount;
		tableMap.put("startpage", st);
		tableMap.put("endpage", ed);

		tableMap.put("colcount", columns.size());
		// }

		// { cols
		root.put("cols", columns);
		// }

		// { rows
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		root.put("rows", list);
		for (Row row : vecRows)
			list.add(row.toMap());
		// }

		return root;
	}

	static Template tmpl;
	static {
		try {
			Configuration cfg = new Configuration();
			cfg.setEncoding(Locale.getDefault(), "UTF-8");
			cfg.setClassForTemplateLoading(TableTag.class, "");
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			tmpl = cfg.getTemplate("table.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int doEndTag() throws JspTagException {
		try {
			synchronized (tmpl) {
				tmpl.process(getTemplateRoot(), getPreviousOut());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return EVAL_PAGE;
	}
}
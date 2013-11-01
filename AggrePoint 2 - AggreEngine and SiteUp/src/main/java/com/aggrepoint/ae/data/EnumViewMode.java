package com.aggrepoint.ae.data;

public enum EnumViewMode {
	EDIT(1, "edit"), // 发布人员可视化编辑页面内容
	PUBLISH(2, "pub"), // 被SitePub调用以发布页面
	VIEW(3, "view"), // 用户查看个性化页面
	RESET(4, "reset"); // 点击页面时重置页面窗口

	int m_iId;
	String m_strName;

	EnumViewMode(int id, String name) {
		m_iId = id;
		m_strName = name;
	}

	public String getName() {
		return m_strName;
	}

	public String getStrId() {
		return Integer.toString(m_iId);
	}

	public int getId() {
		return m_iId;
	}

	public static EnumViewMode fromName(String s) {
		if (s == null)
			return VIEW;

		for (EnumViewMode wm : EnumViewMode.values()) {
			if (wm.getName().equalsIgnoreCase(s))
				return wm;
		}
		return VIEW;
	}

	public static EnumViewMode fromId(int id) {
		for (EnumViewMode wm : EnumViewMode.values()) {
			if (wm.getId() == id)
				return wm;
		}
		return VIEW;
	}

	public static EnumViewMode fromStrId(String id) {
		if (id == null)
			return VIEW;

		for (EnumViewMode wm : EnumViewMode.values()) {
			if (wm.getStrId().equalsIgnoreCase(id))
				return wm;
		}
		return VIEW;
	}

}

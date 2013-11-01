package com.aggrepoint.adk;


/**
 * 窗口模式。
 * 
 * @author Jim
 */
public enum EnumWinMode {
	/** 无变化 */
	NOCHANGE(0, ""),
	/** 正常 */
	NORMAL(1, "normal"),
	/** 最小化 */
	MIN(2, "min"),
	/** 最大化 */
	MAX(3, "max"),
	/** 隐藏 */
	HIDE(4, "hide");

	int m_iId;
	String m_strName;

	EnumWinMode(int id, String name) {
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

	public EnumWinMode getRealMode() {
		if (m_iId < NORMAL.getId())
			return NORMAL;
		if (m_iId > MAX.getId())
			return MAX;
		return this;
	}

	public static EnumWinMode fromName(String s) {
		if (s == null)
			return NOCHANGE;

		for (EnumWinMode wm : EnumWinMode.values()) {
			if (wm.getName().equalsIgnoreCase(s))
				return wm;
		}
		return NOCHANGE;
	}

	public static EnumWinMode fromId(int id) {
		for (EnumWinMode wm : EnumWinMode.values()) {
			if (wm.getId() == id)
				return wm;
		}
		return NOCHANGE;
	}

	public static EnumWinMode fromStrId(String id) {
		if (id == null)
			return NOCHANGE;

		for (EnumWinMode wm : EnumWinMode.values()) {
			if (wm.getStrId().equalsIgnoreCase(id))
				return wm;
		}
		return NOCHANGE;
	}
}

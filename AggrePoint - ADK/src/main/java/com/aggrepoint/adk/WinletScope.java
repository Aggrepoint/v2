package com.aggrepoint.adk;


public enum WinletScope {
	INSTANCE("instance", 0), PAGE("page", 1), SESSION("session", 2);

	String m_strName;
	int m_iScope;

	WinletScope(String str, int i) {
		m_strName = str;
		m_iScope = i;
	}

	public String getName() {
		return m_strName;
	}

	public int getScope() {
		return m_iScope;
	}

	public static WinletScope fromName(String name) {
		if (name == null)
			return INSTANCE;

		for (WinletScope scope : WinletScope.values()) {
			if (scope.getName().equalsIgnoreCase(name))
				return scope;
		}
		return INSTANCE;
	}
}

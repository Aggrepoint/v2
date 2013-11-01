package com.aggrepoint.adk.ui.data;

import java.util.HashSet;
import java.util.Vector;

import com.icebean.core.adb.ADB;
import com.icebean.core.locale.LocaleManager;
import com.icebean.core.strexp.FlagMatcher;

/**
 * @author Owner
 * 
 */
public class Matchable extends ADB {
	public String m_strLSID;
	public String m_strMarkup;

	String m_strFlag;
	FlagMatcher m_matcher;

	public String getFlag() {
		return m_strFlag;
	}

	public void setFlag(String flag) {
		m_strFlag = flag;
		try {
			m_matcher = null;
			if (flag == null)
				return;
			flag = flag.trim();
			if (flag.equals(""))
				return;
			m_matcher = new FlagMatcher(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean imatch(Vector<String> lss, String markup, HashSet<String> flags,
			HashSet<String> flags1, HashSet<String> flags2,
			HashSet<String> flags3) {
		if ((m_strLSID == null || m_strLSID.equals("") || lss
				.contains(m_strLSID))
				&& (m_strMarkup == null || m_strMarkup.equals("") || markup != null
						&& m_strMarkup.equalsIgnoreCase(markup))) {
			if (m_matcher == null)
				return true;

			HashSet<String> all = new HashSet<String>();
			if (flags != null)
				all.addAll(flags);
			if (flags1 != null)
				all.addAll(flags1);
			if (flags2 != null)
				all.addAll(flags2);
			if (flags3 != null)
				all.addAll(flags3);

			return m_matcher.match(all);
		}

		return false;
	}

	public static <T extends Matchable> T match(Vector<T> vec, String markup,
			HashSet<String> flags, HashSet<String> flags1,
			HashSet<String> flags2, HashSet<String> flags3) {
		Vector<String> lss = LocaleManager.getLSIDs(Matchable.class, null);
		for (T t : vec) {
			if (t.imatch(lss, markup, flags, flags1, flags2, flags3))
				return t;
		}

		return null;
	}

	public static <T extends Matchable> Vector<T> matchAll(Vector<T> vec,
			String markup, HashSet<String> flags, HashSet<String> flags1,
			HashSet<String> flags2, HashSet<String> flags3) {
		Vector<String> lss = LocaleManager.getLSIDs(Matchable.class, null);
		Vector<T> matched = new Vector<T>();
		for (T t : vec) {
			if (t.imatch(lss, markup, flags, flags1, flags2, flags3))
				matched.add(t);
		}

		return matched;
	}
}

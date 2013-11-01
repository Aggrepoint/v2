package com.aggrepoint.adk.plugin;

import java.util.StringTokenizer;

import com.aggrepoint.adk.EnumMarkup;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IPsnEngine;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.taglib.html.MarkupTag;
import com.icebean.core.locale.LocaleManager;
import com.icebean.core.strexp.ExpExecException;
import com.icebean.core.strexp.ExpFormatException;
import com.icebean.core.strexp.IExecutor;
import com.icebean.core.strexp.ITranslator;
import com.icebean.core.strexp.StrExpEngine;

/**
 * 缺省的个性化引擎
 * 
 * @author YJM
 */
public class SimplePsnEngine implements IPsnEngine, ITranslator, IExecutor {
	public static final String CMD_EQ = "=";
	public static final String CMD_EQI = "~=";
	public static final String CMD_GT = ">";
	public static final String CMD_GTE = ">=";
	public static final String CMD_LT = "<";
	public static final String CMD_LTE = "<";
	public static final String CMD_AND = "and";
	public static final String CMD_OR = "or";
	public static final String CMD_NOT = "not";
	public static final String CMD_IN = "in";
	public static final String CMD_CONTAINS = "contains";
	public static final String CMD_DELIMITED_START = "dstart";
	public static final String CMD_DELIMITED_END = "dend";
	public static final String CMD_DELIMITED_EQ = "deq";
	public static final String CMD_EMPTY = "empty";

	/** 请求 */
	protected IModuleRequest m_request;
	/** 用户 */
	protected IUserProfile m_userProfile;
	/** 字符串表达式执行引擎 */
	protected StrExpEngine m_strExpEngine;

	/**
	 * @see com.aggrepoint.adk.IPsnEngine#init(IModuleRequest, IUserProfile)
	 */
	public void init(IModuleRequest request, IUserProfile userProfile) {
		m_request = request;
		m_userProfile = userProfile;
		ITranslator[] it = { this };
		IExecutor[] ie = { this };
		m_strExpEngine = new StrExpEngine(it, ie);
	}

	public EnumMarkup getMarkup() {
		return MarkupTag.getMarkup(m_request);
	}

	public String translate(String value) {
		if (value.startsWith("user.")) {
			value = m_userProfile.getProperty(value.substring(5));
			if (value == null)
				return "";

			return value;
		} else if (value.startsWith("request.")) {
			value = value.substring(8);
			if (value.equalsIgnoreCase("ip"))
				return m_request.getRemoteAddr();
			else if (value.equalsIgnoreCase("localeset")) {
				return LocaleManager.getLSID(SimplePsnEngine.class,
						m_request.getLocaleStr());
			} else if (value.startsWith("param.")) {
				value = value.substring(6);
				return m_request.getParameterNotMultipart(value, "");
			}
		}

		return null;
	}

	/** 判断该Executor是否能处理指定的操作 */
	public boolean match(String cmd) {
		if (cmd.equalsIgnoreCase(CMD_EQ) || cmd.equalsIgnoreCase(CMD_EQI)
				|| cmd.equalsIgnoreCase(CMD_GT)
				|| cmd.equalsIgnoreCase(CMD_GTE)
				|| cmd.equalsIgnoreCase(CMD_LT)
				|| cmd.equalsIgnoreCase(CMD_LTE)
				|| cmd.equalsIgnoreCase(CMD_AND)
				|| cmd.equalsIgnoreCase(CMD_OR)
				|| cmd.equalsIgnoreCase(CMD_NOT)
				|| cmd.equalsIgnoreCase(CMD_IN)
				|| cmd.equalsIgnoreCase(CMD_EMPTY)
				|| cmd.equalsIgnoreCase(CMD_CONTAINS)
				|| cmd.equalsIgnoreCase(CMD_DELIMITED_START)
				|| cmd.equalsIgnoreCase(CMD_DELIMITED_END)
				|| cmd.equalsIgnoreCase(CMD_DELIMITED_EQ))
			return true;

		return false;
	}

	/**
	 * 执行操作
	 * 
	 * @param params
	 *            String[] 参数数组
	 * @param start
	 *            int 第一个参数下标
	 * @param end
	 *            int 最后一个参数下标。若start > end 表示没有任何参数
	 */
	public String execute(String cmd, String[] params, int start, int end)
			throws Exception {
		if (cmd.equalsIgnoreCase(CMD_EQ)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);
			return params[start].equals(params[end]) ? "T" : "F";
		} else if (cmd.equalsIgnoreCase(CMD_EQI)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);
			return params[start].equalsIgnoreCase(params[end]) ? "T" : "F";
		} else if (cmd.equalsIgnoreCase(CMD_GT)
				|| cmd.equalsIgnoreCase(CMD_GTE)
				|| cmd.equalsIgnoreCase(CMD_LT)
				|| cmd.equalsIgnoreCase(CMD_LTE)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			double d1 = 0, d2 = 0;
			try {
				d1 = Double.parseDouble(params[start]);
			} catch (Exception e) {
			}
			try {
				d2 = Double.parseDouble(params[end]);
			} catch (Exception e) {
			}

			if (cmd.equalsIgnoreCase(CMD_GT))
				return d1 > d2 ? "T" : "F";
			if (cmd.equalsIgnoreCase(CMD_GTE))
				return d1 >= d2 ? "T" : "F";
			if (cmd.equalsIgnoreCase(CMD_LT))
				return d1 < d2 ? "T" : "F";
			return d1 <= d2 ? "T" : "F";
		} else if (cmd.equalsIgnoreCase(CMD_AND)) {
			if (end - start <= 0)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			for (; start <= end; start++)
				if (!params[start].equalsIgnoreCase("T"))
					return "F";
			return "T";
		} else if (cmd.equalsIgnoreCase(CMD_OR)) {
			if (end - start <= 0)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			for (; start <= end; start++)
				if (params[start].equalsIgnoreCase("T"))
					return "T";
			return "F";
		} else if (cmd.equalsIgnoreCase(CMD_NOT)) {
			if (end != start)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			if (params[start].equalsIgnoreCase("T"))
				return "F";
			else if (params[start].equalsIgnoreCase("F"))
				return "T";
			else
				throw new ExpExecException(ExpExecException.TYPE_INVALID_PARAM,
						cmd, null, 0);
		} else if (cmd.equalsIgnoreCase(CMD_IN)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			return params[end].indexOf(params[start]) == -1 ? "F" : "T";
		} else if (cmd.equalsIgnoreCase(CMD_EMPTY)) {
			if (end != start)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			if (params[start] == null || params[start].equals("")
					|| params[start].trim().equals(""))
				return "T";
			return "F";
		} else if (cmd.equalsIgnoreCase(CMD_CONTAINS)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			return params[start].indexOf(params[end]) == -1 ? "F" : "T";
		} else if (cmd.equalsIgnoreCase(CMD_DELIMITED_START)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			StringTokenizer st = new StringTokenizer(params[start], ", ");
			while (st.hasMoreTokens())
				if (st.nextToken().startsWith(params[end]))
					return "T";
			return "F";
		} else if (cmd.equalsIgnoreCase(CMD_DELIMITED_END)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			StringTokenizer st = new StringTokenizer(params[start], ", ");
			while (st.hasMoreTokens())
				if (st.nextToken().endsWith(params[end]))
					return "T";
			return "F";
		} else if (cmd.equalsIgnoreCase(CMD_DELIMITED_EQ)) {
			if (end - start != 1)
				throw new ExpExecException(
						ExpExecException.TYPE_PARAM_NUM_ERROR, cmd, null, 0);

			StringTokenizer st = new StringTokenizer(params[start], ", ");
			while (st.hasMoreTokens())
				if (st.nextToken().equals(params[end]))
					return "T";
			return "F";
		}

		return null;
	}

	/**
	 * 不执行实际的操作，检查参数个数是否符合要求
	 * 
	 * @param params
	 *            String[] 参数数组
	 * @param start
	 *            int 第一个参数下标
	 * @param end
	 *            int 最后一个参数下标。若start > end 表示没有任何参数
	 */
	public boolean syntaxCheck(String cmd, String[] params, int start, int end) {
		if (cmd.equalsIgnoreCase(CMD_EQ) || cmd.equalsIgnoreCase(CMD_EQI)
				|| cmd.equalsIgnoreCase(CMD_GT)
				|| cmd.equalsIgnoreCase(CMD_GTE)
				|| cmd.equalsIgnoreCase(CMD_LT)
				|| cmd.equalsIgnoreCase(CMD_LTE)) {
			if (end - start != 1)
				return false;
			return true;
		} else if (cmd.equalsIgnoreCase(CMD_AND)) {
			if (end - start <= 0)
				return false;
			return true;
		} else if (cmd.equalsIgnoreCase(CMD_OR)) {
			if (end - start <= 0)
				return false;
			return true;
		} else if (cmd.equalsIgnoreCase(CMD_NOT)) {
			if (end != start)
				return false;
			return true;
		} else if (cmd.equalsIgnoreCase(CMD_IN)) {
			if (end - start != 1)
				return false;
			return true;
		} else if (cmd.equalsIgnoreCase(CMD_EMPTY)) {
			if (end != start)
				return false;
			return true;
		} else if (cmd.equalsIgnoreCase(CMD_CONTAINS)) {
			if (end - start != 1)
				return false;
			return true;
		}

		return false;
	}

	/**
	 * @see com.aggrepoint.adk.IPsnEngine#eveluate(String)
	 */
	public boolean eveluate(String rule) {
		try {
			return "T".equalsIgnoreCase(m_strExpEngine.execute(rule));
		} catch (Exception e) {
			System.err.println(rule);
			e.printStackTrace();
		}

		return false;
	}

	public void syntaxCheck(String rule) throws ExpFormatException {
		m_strExpEngine.syntaxCheck(rule);
	}

	/** 创建克隆 */
	public IPsnEngine newClone() {
		SimplePsnEngine engine = new SimplePsnEngine();
		engine.init(m_request, m_userProfile);
		return engine;
	}

	public IModuleRequest getRequest() {
		return m_request;
	}

	public IUserProfile getUserProfile() {
		return m_userProfile;
	}

	public void setRequest(IModuleRequest req) {
		m_request = req;
	}

	public void setUserProfile(IUserProfile userProfile) {
		m_userProfile = userProfile;
	}
}

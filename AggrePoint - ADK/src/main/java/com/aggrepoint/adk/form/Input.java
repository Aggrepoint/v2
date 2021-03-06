package com.aggrepoint.adk.form;

import java.util.Vector;

import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.ui.ValidateResultType;

public interface Input {
	public String getType();

	public String getName();

	public boolean isHasError();

	public boolean isDisabled();

	public String getErrorValue();

	public Form getForm();

	public Vector<String> getErrors();

	public String getError();

	public void clearErrors();

	/**
	 * 获取输入项当前用于前台展示的值（例如日期已按格式转换为字符串）。如果有输入错误则返回导致输入错误的值。
	 * 
	 * @return
	 */
	public Object getValue();

	/**
	 * 获取输入项的当前值（不包含输入错误，未转换为前台展示）
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getPropValue() throws Exception;

	public ValidateResultType populate(IModuleRequest req);

	public ValidateResultType populate(IModuleRequest req, String value);

	public ValidateResultType populate(IModuleRequest req, String[] value);
}

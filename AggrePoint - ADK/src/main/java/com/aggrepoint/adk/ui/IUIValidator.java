package com.aggrepoint.adk.ui;

import java.util.Vector;

import com.aggrepoint.adk.ui.data.Property;

/**
 * @author YJM
 */
public interface IUIValidator {
	/**
	 * 
	 * @param obj
	 * @param property
	 * @param args
	 * @return false表示检验未通过，true表示检验通过
	 * @throws Exception
	 */
	public boolean validate(Object obj, Property property, Vector<String> args)
			throws Exception;
}

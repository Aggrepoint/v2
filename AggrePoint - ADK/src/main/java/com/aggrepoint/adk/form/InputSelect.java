package com.aggrepoint.adk.form;

import java.util.Map;
import java.util.Vector;

import com.aggrepoint.adk.ui.SelectOption;

public interface InputSelect extends Input {
	public void updateOptions(Map<String, String> options);

	public Vector<SelectOption> getOptions();
}

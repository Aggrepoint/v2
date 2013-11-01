package com.aggrepoint.adk.ui.data;

import org.w3c.dom.Element;

import com.icebean.core.adb.ADB;

public class EditFactory extends ADB {

	public boolean isFactory() {
		return true;
	}

	public ADB newInstance(Element elm) throws IllegalAccessException,
			InstantiationException {
		ADB adb = null;

		String type = elm.getTagName();

		if (type.equalsIgnoreCase("hidden"))
			adb = new EditHidden();
		else if (type.equalsIgnoreCase("checkbox"))
			adb = new EditCheckBox();
		else if (type.equalsIgnoreCase("radio"))
			adb = new EditRadio();
		else if (type.equalsIgnoreCase("select"))
			adb = new EditSelect();
		else if (type.equalsIgnoreCase("subsel"))
			adb = new EditSubSel();
		else if (type.equalsIgnoreCase("iconsel"))
			adb = new EditIconSel();
		else if (type.equalsIgnoreCase("file"))
			adb = new EditFile();
		else if (type.equalsIgnoreCase("text"))
			adb = new EditText();
		else if (type.equalsIgnoreCase("textarea"))
			adb = new EditTextArea();
		else if (type.equalsIgnoreCase("date"))
			adb = new EditDate();
		else if (type.equalsIgnoreCase("number"))
			adb = new EditNumber();
		else if (type.equalsIgnoreCase("password"))
			adb = new EditPassword();
		else if (type.equalsIgnoreCase("vcode"))
			adb = new EditVcode();
		else if (type.equalsIgnoreCase("custom"))
			adb = new EditCustom();

		if (adb == null && !type.equalsIgnoreCase("validator"))
			System.err.println("Encounter unknow edit tag: " + type);

		if (adb != null)
			adb.m_objCreator = this;
		return adb;
	}
}

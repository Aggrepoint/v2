package com.aggrepoint.adk.form;

public class Change {
	String input;
	String type;

	protected Change(String input, String type) {
		this.input = input;
		this.type = type;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

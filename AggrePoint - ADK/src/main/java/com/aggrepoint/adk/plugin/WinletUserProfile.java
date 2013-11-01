package com.aggrepoint.adk.plugin;

import java.io.UnsupportedEncodingException;

import com.aggrepoint.adk.IPropertyObject;
import com.aggrepoint.adk.IUserProfile;
import com.aggrepoint.adk.PropertyObject;

/**
 * 简单的Winlet用户信息
 * 
 * @author YJM
 */
public class WinletUserProfile extends PropertyObject implements IUserProfile {
	public static final String PROPERTY_NAME = "name";

	public static final String PROPERTY_EMAIL = "email";

	public WinletUserProfile() {
		super();
	}

	public WinletUserProfile(IPropertyObject ref) {
		super(ref);
	}

	public WinletUserProfile(String prop) throws UnsupportedEncodingException {
		super(prop);
	}

	@Override
	public String getProperty(String property) {
		if (property == null)
			return null;

		if (property.equalsIgnoreCase(PROPERTY_ISANONYMOUS))
			return isAnonymous() ? "T" : "F";

		return super.getProperty(property);
	}

	@Override
	public boolean hasProperty(String property) {
		if (property == null)
			return false;

		if (property.equalsIgnoreCase(PROPERTY_ISANONYMOUS))
			return true;

		return super.hasProperty(property);
	}

	@Override
	public boolean isAnonymous() {
		return m_htProperties.get(PROPERTY_ID) == null;
	}

	public void setId(String id) {
		this.setProperty(PROPERTY_ID, id);
	}

	public void setName(String name) {
		this.setProperty(PROPERTY_NAME, name);
	}

	public String getId() {
		return getProperty(PROPERTY_ID);
	}

	public String getName() {
		return getProperty(PROPERTY_NAME);
	}
}

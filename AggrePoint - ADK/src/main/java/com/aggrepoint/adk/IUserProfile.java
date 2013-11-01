package com.aggrepoint.adk;


/**
 * 访问用户身份
 * 
 * @author YJM
 */
public interface IUserProfile extends IPropertyObject {
	/** 必须提供的属性：用户是否匿名 */
	public static final String PROPERTY_ISANONYMOUS = "isanonymous";
	/** 必须提供的属性：用户ID */
	public static final String PROPERTY_ID = "id";
	/** 可选属性：保留的秒数 */
	public static final String KEEP_TIME = "keep";

	/** 判断是否匿名用户 */
	public boolean isAnonymous();
}

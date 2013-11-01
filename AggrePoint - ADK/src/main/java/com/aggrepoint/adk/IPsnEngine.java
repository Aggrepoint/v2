package com.aggrepoint.adk;


/**
 * 个性化引擎
 * 
 * @author YJM
 */
public interface IPsnEngine {
	/** 初始化 */
	public void init(
		IModuleRequest request,
		IUserProfile userProfile);

	/** 评估个性化规则是否匹配 */
	public boolean eveluate(String rule);
	/** 翻译语法元素 */
	public String translate(String value);
	/** 创建克隆 */
	public IPsnEngine newClone();

	public IModuleRequest getRequest();
	public IUserProfile getUserProfile();
	public EnumMarkup getMarkup();

	public void setRequest(IModuleRequest req);
	public void setUserProfile(IUserProfile userProfile);
}

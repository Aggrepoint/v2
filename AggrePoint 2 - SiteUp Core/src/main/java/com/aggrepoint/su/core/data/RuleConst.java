/*
 */
package com.aggrepoint.su.core.data;

/**
 * @author administrator
 */
public interface RuleConst {
	public static final String SU_ROOT = "contains(user.su_roles, root)";
	public static final String SU_GLOBAL = "contains(user.su_roles, global)";
	public static final String SU_CHAN = "contains(user.su_roles, chan)";
}

package com.aggrepoint.su.core.data;

import org.safehaus.uuid.UUIDGenerator;

/**
 * @author YJM
 */
public class UUIDGen {
	static UUIDGenerator uuidgen = UUIDGenerator.getInstance();

	public static String get() {
		return uuidgen.generateTimeBasedUUID().toString();
	}
}

package com.aggrepoint.su.data;

import java.io.File;
import java.util.Vector;

import com.aggrepoint.adk.ui.ValidateResult;
import com.icebean.core.adb.ADB;

/**
 * 
 * @author YJM
 */
public class ResUpload extends ADB {
	public String m_strPath;
	public boolean m_bCleanExisting = true;

	public static ValidateResult checkPath(ResUpload item, Vector<String> args) {
		File file = new File(item.m_strPath);
		if (file.exists() && file.isDirectory())
			return ValidateResult.PASS;

		return ValidateResult.FAILED;
	}
}

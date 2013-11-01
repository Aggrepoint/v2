package com.aggrepoint.su.winlet;

import com.aggrepoint.adk.FileParameter;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IModuleResponse;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.form.Input;
import com.aggrepoint.adk.plugin.WinletUserProfile;
import com.aggrepoint.adk.ui.ValidateResult;
import com.aggrepoint.adk.ui.ValidateResultType;
import com.aggrepoint.su.core.data.ApImage;
import com.aggrepoint.su.core.data.RuleConst;
import com.icebean.core.adb.db.DbAdapter;

/**
 * 
 * 
 * @author YJM
 */
public class WinletBase extends Winlet implements RuleConst {
	private static final long serialVersionUID = 1L;

	public int dummy(IModuleRequest req, IModuleResponse resp) throws Exception {
		return 0;
	}

	public long getSiteId(IModuleRequest req) {
		String id = ((WinletUserProfile) req.getUserProfile())
				.getProperty("site");
		if (id == null || id.equals(""))
			return 0;
		return Long.parseLong(id);
	}

	public ValidateResult validateImage(IModuleRequest req, Input inp, long[] id)
			throws Exception {
		FileParameter logoFile = (FileParameter) inp.getPropValue();
		if (logoFile == null)
			return ValidateResult.PASS;

		if (logoFile.m_lSize <= 0)
			return new ValidateResult(ValidateResultType.FAILED_SKIP_PROPERTY,
					req.getMessage("logotoolarge", ""));

		if (!logoFile.m_strContentType.startsWith("image"))
			return new ValidateResult(ValidateResultType.FAILED_SKIP_PROPERTY,
					req.getMessage("logoformatwrong", ""));

		if (logoFile.m_strContentType.indexOf("jpeg") < 0
				&& logoFile.m_strContentType.indexOf("gif") < 0)
			return new ValidateResult(ValidateResultType.FAILED_SKIP_PROPERTY,
					req.getMessage("logoformatwrong", ""));

		ApImage image = new ApImage();
		// Not deleting existing image from DB because of if something
		// goes wrong and user don't save the changes then the user will
		// still be associating with existing image

		image.m_iImageType = ApImage.IMGTYPE_TEMPLATE;
		image.m_strImageName = logoFile.getFileName();
		image.m_strContentType = logoFile.m_strContentType;
		image.m_strImage = logoFile.m_strFullPath;
		new DbAdapter(req.getDBConn()).create(image);

		id[0] = image.m_lImageID;

		return ValidateResult.PASS;
	}
}

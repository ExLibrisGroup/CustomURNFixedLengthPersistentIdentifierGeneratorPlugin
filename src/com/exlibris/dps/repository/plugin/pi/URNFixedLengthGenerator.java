package com.exlibris.dps.repository.plugin.pi;

import java.text.DecimalFormat;
import java.util.Map;

import com.exlibris.core.infra.svc.api.GeneralParameterManager;
import com.exlibris.core.infra.svc.api.locator.ServiceLocator;
import com.exlibris.core.repository.ifc.SequenceManager;
import com.exlibris.digitool.common.pi.URN;
import com.exlibris.dps.sdk.plugin.PIGenerator;

public class URNFixedLengthGenerator implements PIGenerator {

	private String prefix;

	public String getType() {
		return URN.getType();
	}

	public URNFixedLengthGenerator() {
		super();
	}

	@Override
	public String getValue() {

		Long seq = getSequenceManager().fetchNextVal("URN_SEQUENCE");

		if (seq == null)
			return (null);

		String urn = prefix;

		if (!urn.endsWith("-")) {
			urn = urn + "-";
		}

		StringBuffer sformat = new StringBuffer();
		int i, size = 9;

		try {
			size = Integer.parseInt(getGeneralParamater(
					GeneralParameterManager.MODULE_REPOSITORY,
					"urn_fixed_length"));
		}catch (Exception e) {
		    size = 9;
        }

		for (i = 0; i < size; i++) {
		    sformat.append("0");
		}

		DecimalFormat df = new DecimalFormat(sformat.toString());
		urn = urn + df.format(seq);

		return (urn + URN.getCheckDigit(urn));
	}

	public boolean validate(String urn) {
		if (!URN.isValidURN(urn))
			return false;

		String dig = urn.substring(urn.length() - 1, urn.length());
		String str = urn.substring(0, urn.length() - 1);
		if (!dig.equals(URN.getCheckDigit(str)))
			return false;

		return true;
	}

	public void initParams(Map<String, String> initParams) {
		prefix = initParams.get("prefix");
	}

	private String getGeneralParamater(String module, String paramName) {

		GeneralParameterManager manager =
			ServiceLocator.getInstance().lookUp(GeneralParameterManager.class);

		return manager.getParameter(module, paramName);
	}

	private SequenceManager getSequenceManager() {
		return ServiceLocator.getInstance().lookUp(SequenceManager.class);
	}

}

package org.sitenv.xml.validators.ccda;


public enum CcdaValidatorExpectedValuesType
{
	DISPLAY_NAMES_FOR_CODE("displayNamesForCode"), 
	CODE_SYSTEM_NAMES_FOR_CODE_SYSTEM("codeSystemNamesForCodeSystem"),
	CODE_SYSTEMS_FOR_CODE_SYSTEM_NAME("codeSystemsForCodeSystemName"),
	CODES_FOR_DISPLAY_NAMES("codesForDisplayNames"),
	CODE_SYSTEMS_FOR_CODE("codeSystemsForCode"),
	CODE_SYSTEMS_FOR_VALUE_SET("codeSystemsForValueSet"),
	CODE_SYSTEMS_FOR_INVALID_CODE_SYSTEM("codeSystemsForInvalidCodeSystem");
	
	private String value;
	
	private CcdaValidatorExpectedValuesType (String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return this.value;
	}
}

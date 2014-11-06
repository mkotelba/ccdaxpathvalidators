package org.sitenv.xml.validators.ccda;



import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.data.DisplayNameValidationResult;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.xml.xpathvalidator.engine.XPathNodeValidator;
import org.w3c.dom.Node;

public class CcdaCodeValidator implements XPathNodeValidator {

	private static final Logger logger = Logger.getLogger(CcdaCodeValidator.class);
	
	public CcdaValidatorResult validateNode(String expression, XPath xpath, Node node, int nodeIndex) {

		try
		{
			XPathExpression expCode = xpath.compile("@code");
			XPathExpression expCodeSystem = xpath.compile("@codeSystem");
			XPathExpression expCodeSystemName = xpath.compile("@codeSystemName");
			XPathExpression expDisplayName = xpath.compile("@displayName");
			
			String code = (String)expCode.evaluate(node, XPathConstants.STRING);
			logger.trace("code: " + code);
			
			String codeSystem = (String)expCodeSystem.evaluate(node, XPathConstants.STRING);
			logger.trace("codeSystem: " + codeSystem);
			
			String codeSystemName = (String)expCodeSystemName.evaluate(node, XPathConstants.STRING);
			logger.trace("codeSystemName: " + codeSystemName);
			
			String displayName = (String)expDisplayName.evaluate(node, XPathConstants.STRING);
			logger.trace("displayName: " + displayName);
			
			if (ValidationEngine.isCodeSystemLoaded(codeSystem)) {
			
				boolean isValidCode = ValidationEngine.validateCode(codeSystem, code);
				boolean isValidDisplayName = ValidationEngine.validateDisplayName(codeSystem, displayName);
				
				DisplayNameValidationResult dnResult = ValidationEngine.validateDisplayNameForCode(codeSystem, displayName, code);
				boolean isDisplayNameMatch =  false;
				
				if (dnResult != null)
				{
					isDisplayNameMatch = dnResult.isResult() && isValidCode;
				}
				
				CcdaValidatorResult result = new CcdaValidatorResult(); 
				
				result.setCode(code);
				result.setCodeSystem(codeSystem);
				result.setCodeSystemName(codeSystemName);
				result.setDisplayName(displayName);
				result.setXpathExpression(expression);
				
				result.setNodeIndex(nodeIndex);
				
				
				if (!isValidCode) {
					result.setError(true);
					result.setErrorMessage("Code '" + code + "' does not exist in vocabulary '" + codeSystemName + "' (" + codeSystem + ")");
				}
				
				if (!isValidDisplayName && result.getDisplayName() != null && !result.getDisplayName().trim().equals("")) {
					result.setWarning(true);
					result.setWarningMessage("DisplayName '" + displayName + "' does not (fully) exist in vocabulary '" + codeSystemName + "' (" + codeSystem + ")");
				}
				
				else if (!isDisplayNameMatch) {
					result.setInformation(true);
					if (dnResult != null && dnResult.getActualDisplayName() != null)
					{
						result.setInfoMessage("DisplayName '" + displayName + "' for code '" + code + "' did not (fully) match the anticipated value of '" + dnResult.getActualDisplayName() + "' in vocabulary '" + codeSystemName + "' (" + codeSystem + ")");
					}
					else
					{
						result.setInfoMessage("DisplayName for code '" + code + "' does not exist in vocabulary '" + codeSystemName + "' (" + codeSystem + ")");
					}
					
				
				}
				
				
				return result;
			}
			else 
			{
				CcdaValidatorResult result = new CcdaValidatorResult();
				
				// TODO: any additional information due to a code system not existing in the vocabulary service
				
				return result;
			}
			
		}
		catch (XPathExpressionException e)
		{
			logger.error(e);
		}
		
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
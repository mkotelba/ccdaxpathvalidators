package org.sitenv.xml.validators.ccda;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.data.CodeValidationResult;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.xml.xpathvalidator.engine.data.XPathValidatorResult;
import org.w3c.dom.Node;

public class CcdaCodeValidator {

	private static final Logger logger = Logger.getLogger(CcdaCodeValidator.class);
	
	public List<XPathValidatorResult> validateNode(String baseExpression, int baseNodeIndex, String expression, XPath xpath, Node node, int nodeIndex, Map<String, String> params) {

		List<XPathValidatorResult> list = new ArrayList<XPathValidatorResult>();
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
			
			CcdaValidatorResult result;
			
			if (ValidationEngine.isCodeSystemLoaded(codeSystem)) {
				
				CodeValidationResult validation = ValidationEngine.validateCode(codeSystem, codeSystemName, code, displayName);
				
				
				Boolean errors = false, warnings=false;
				
				
				if (!validation.getCodeSystemAndNameMatch())
				{
					result = createNewResult(code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					warnings = true;
					result.setWarning(true);
					result.setWarningMessage("Code system name '" + codeSystemName + "' does not match expected name for code system oid (" + codeSystem + ")");
					result.setExpectedValues(validation.getExpectedCodeSystemNamesForOid());
					result.setExpectedValuesType(CcdaValidatorExpectedValuesType.CODE_SYSTEM_NAMES_FOR_CODE_SYSTEM);
					list.add(result);
					
					
				}
				
				
				if (!validation.getCodeExistsInCodeSystem()) {
					result = createNewResult(code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					errors = true;
					result.setError(true);
					result.setErrorMessage("Code '" + code + "' does not exist in code system " + ((codeSystemName!=null) ? "'" + codeSystemName + "'" :"") + " (" + codeSystem + ")");
					list.add(result);
				}
				
				
				if (validation.getRequestedDisplayName() == null || validation.getRequestedDisplayName().trim().isEmpty())
				{
					result = createNewResult(code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					warnings = true;
					result.setWarning(true);
					result.setWarningMessage("DisplayName is not populated.");
					list.add(result);
					
				}
				else if (!validation.getDisplayNameExistsInCodeSystem()) 
				{
					result = createNewResult(code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					
					warnings = true;
					result.setWarning(true);
					result.setWarningMessage("DisplayName '" + displayName + "' does not (fully) exist in code system " + ((codeSystemName!=null) ? "'" + codeSystemName + "'" :"") + " (" + codeSystem + ")");
	
					list.add(result);
				}
				else if (!validation.getDisplayNameExistsForCode()) 
				{
					
					result = createNewResult(code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					warnings = true;
					result.setWarning(true);
					result.setWarningMessage("DisplayName '" + displayName + "' for code '" + code + "' does not exist in vocabulary '" + codeSystemName + "' (" + codeSystem + ")");
					
					result.setExpectedValues(validation.getExpectedDisplayNamesForCode());

					result.setExpectedValuesType(CcdaValidatorExpectedValuesType.DISPLAY_NAMES_FOR_CODE);
					list.add(result);
				}
				
				if (!errors && !warnings)
				{
					result = createNewResult(code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					result.setInformation(true);
					result.setInfoMessage("Successful code validation.");
					
					
					list.add(result);
				}
				
			}
			else 
			{
				
				// TODO: any additional information due to a code system not existing in the vocabulary service
				result = createNewResult(code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, null);
				
				result.setInformation(true);
				result.setInfoMessage("Code validation attempt of code system that does not exist in service.");
				
				result.setExpectedValues(VocabularyConstants.CODE_SYSTEM_MAP.keySet());

				result.setExpectedValuesType(CcdaValidatorExpectedValuesType.CODE_SYSTEMS_FOR_INVALID_CODE_SYSTEM);
				list.add(result);
			}
			
		}
		catch (XPathExpressionException e)
		{
			logger.error(e);
		}
		
		// TODO Auto-generated method stub
		return list;
	}
	
	private CcdaValidatorResult createNewResult(String code, String codeSystem, String codeSystemName, String displayName, String baseExpression, Integer baseNodeIndex, String expression, Integer nodeIndex, CodeValidationResult validation)
	{
		CcdaValidatorResult result = new CcdaValidatorResult(); 
		
		result.setRequestedCode(code);
		result.setRequestedCodeSystem(codeSystem);
		result.setRequestedCodeSystemName(codeSystemName);
		result.setRequestedDisplayName(displayName);
		result.setBaseXpathExpression(baseExpression);
		result.setBaseNodeIndex(baseNodeIndex);
		result.setXpathExpression(expression);
		result.setNodeIndex(nodeIndex);
		
		
		return result;
	}
	

}

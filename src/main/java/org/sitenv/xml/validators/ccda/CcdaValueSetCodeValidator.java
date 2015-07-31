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
import org.sitenv.vocabularies.data.ValueSetValidationResult;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.xml.xpathvalidator.engine.data.XPathValidatorResult;
import org.w3c.dom.Node;

public class CcdaValueSetCodeValidator {

	private static final Logger logger = Logger.getLogger(CcdaValueSetCodeValidator.class);
	
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
			
			String valueSet = params.get("valueSet");
			logger.trace("valueSet: " + params.get("valueSet"));
			
			CcdaValidatorResult result;
			String valueSetName = null;
			
			
			if (ValidationEngine.isValueSetLoaded(valueSet))
			{
				
				ValueSetValidationResult validation = ValidationEngine.validateValueSetCode(valueSet, codeSystem, codeSystemName, codeSystemName, displayName);
				
				if (validation.getValueSetNames() != null && !validation.getValueSetNames().isEmpty())
				{
					valueSetName = validation.getValueSetNames().get(0);
				}
				
				Boolean errors = false, warnings = false;
				
				if (!validation.getCodeSystemExistsInValueSet())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);	
					
					errors = true;
					result.setError(true);
					result.setErrorMessage("Code system '" + codeSystem + "' does not exist in the value set (" + valueSet + ")");
					result.setExpectedValues(validation.getExpectedCodeSystemsForValueSet());
					list.add(result);
				}
				else if (!validation.getCodeSystemAndNameMatch())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);	
					
					warnings = true;
					result.setWarning(true);
					result.setWarningMessage("Code system name '" + codeSystemName + "' does not match expected name for code system oid (" + codeSystem + ") in the value set (" + valueSet + ")");
					result.setExpectedValues(validation.getExpectedCodeSystemNamesForOid());
					list.add(result);
				}
				
				if (!validation.getCodeExistsInValueSet())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);	
					
					errors = true;
					result.setError(true);
					result.setErrorMessage("Code '" + code + "' does not exist in the value set (" + valueSet + ")");
					
					list.add(result);
				}
				else if (!validation.getCodeExistsInCodeSystem())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);	
					
					errors = true;
					result.setError(true);
					result.setErrorMessage("Code '" + code + "' does not exist in code system " + ((codeSystemName!=null) ? "'" + codeSystemName + "'" :"") + " (" + codeSystem + ") in the value set (" + valueSet + ")");
					
					list.add(result);
				}
				
				
				
				if (validation.getRequestedDescription() == null || validation.getRequestedDescription().trim().isEmpty())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					warnings = true;
					result.setWarning(true);
					result.setWarningMessage("DisplayName is not populated.");
					list.add(result);
					
				}
				else if (!validation.getDescriptionExistsInValueSet())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);	
					
					errors = true;
					result.setWarning(true);
					result.setWarningMessage("DisplayName '" + displayName + "' does not exist in the value set (" + valueSet + ")");
					
					list.add(result);
				}
				else if (!validation.getDescriptionExistsInCodeSystem())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);	
					
					warnings = true;
					result.setWarning(true);
					result.setWarningMessage("DisplayName '" + displayName + "' does not exist in code system " + ((codeSystemName!=null) ? "'" + codeSystemName + "'" :"") + " (" + codeSystem + ") in the value set (" + valueSet + ")");
					
					list.add(result);
				}
				else if (!validation.getDescriptionExistsInValueSet())
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);	
					
					errors = true;
					result.setWarning(true);
					result.setWarningMessage("DisplayName '" + displayName + "' for code '" + code + "' does not exist in vocabulary '" + codeSystemName + "' (" + codeSystem + ") in the value set (" + valueSet + ")");
					result.setExpectedValues(validation.getExpectedDescriptionsForCode());
					list.add(result);
				}
				
				if (!errors && !warnings)
				{
					result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, validation);
					
					result.setInformation(true);
					result.setInfoMessage("Successful value set code validation.");
					
					
					list.add(result);
				}
				
			}
			else 
			{
				
				// TODO: any additional information due to a code system not existing in the vocabulary service
				result = createNewResult(valueSet, valueSetName, code, codeSystem, codeSystemName, displayName, baseExpression, baseNodeIndex, expression, nodeIndex, null);
				
				result.setInformation(true);
				result.setInfoMessage("Value set code validation attempt for a value set that does not exist in service.");
				
				result.setExpectedValues(VocabularyConstants.CODE_SYSTEM_MAP.keySet());
				
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
	
	private CcdaValidatorResult createNewResult(String valueSet, String valueSetName, String code, String codeSystem, String codeSystemName, String displayName, String baseExpression, Integer baseNodeIndex, String expression, Integer nodeIndex, ValueSetValidationResult validation)
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
		result.setRequestedValueSet(valueSet);
		result.setValueSetName(valueSetName);
		
		
		return result;
	}
}

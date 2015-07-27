package org.sitenv.xml.validators.ccda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.data.DisplayNameValidationResult;
import org.sitenv.vocabularies.engine.ValidationEngine;
import org.sitenv.vocabularies.model.ValueSetModel;
import org.sitenv.xml.xpathvalidator.engine.MultipleXPathNodeValidator;
import org.sitenv.xml.xpathvalidator.engine.data.XPathValidatorResult;
import org.w3c.dom.Node;

public class CcdaValueSetCodeValidator implements MultipleXPathNodeValidator {

	private static final Logger logger = Logger.getLogger(CcdaValueSetCodeValidator.class);
	
	public List<XPathValidatorResult> validateNode(String expression, XPath xpath, Node node, int nodeIndex, Map<String, String> params) {

		List<XPathValidatorResult> list = new ArrayList<XPathValidatorResult>();
		
		try
		{
			XPathExpression expCode = xpath.compile("@code");
			XPathExpression expCodeSystem = xpath.compile("@codeSystem");
			XPathExpression expCodeSystemName = xpath.compile("@codeSystemName");
			XPathExpression expDisplayName = xpath.compile("@displayName");
			
			String valueSet = params.get("valueSet");
			String validateCodeSystem = params.get("validateCodeSystem");
			Boolean validatingCodeSystem = false;
			
			if (validateCodeSystem != null)
			{
				validatingCodeSystem = Boolean.getBoolean(validateCodeSystem.toLowerCase());
			}
			
			
			
			String code = (String)expCode.evaluate(node, XPathConstants.STRING);
			logger.trace("code: " + code);
			
			String codeSystem = (String)expCodeSystem.evaluate(node, XPathConstants.STRING);
			logger.trace("codeSystem: " + codeSystem);
			
			String codeSystemName = (String)expCodeSystemName.evaluate(node, XPathConstants.STRING);
			logger.trace("codeSystemName: " + codeSystemName);
			
			String displayName = (String)expDisplayName.evaluate(node, XPathConstants.STRING);
			logger.trace("displayName: " + displayName);
			
			//  TODO: should we add in the validation of the Display Name/Code System Name?  I think so
			List<ValueSetModel> models = ValidationEngine.getValueSetCode(valueSet, code, codeSystem);
			
			if (models != null ) {
			
				for (ValueSetModel model : models)
				{
					if (model.getCodeSystemName() != null && codeSystemName != null && codeSystemName.equalsIgnoreCase(model.getCodeSystemName()))
					{
						// info message for code system name matching
					}
					else if ((model.getCodeSystemName() == null || model.getCodeSystemName() != null && model.getCodeSystemName().trim().equals(""))
							&& (codeSystemName == null || codeSystemName != null && codeSystemName.trim().equals("")))
					{
					
						// info message for code system name not being evaluated
							
					}
				}
			}
			else 
			{
				
				// TODO: any additional information due to a code system not existing in the vocabulary service
				
			}
			
		}
		catch (XPathExpressionException e)
		{
			logger.error(e);
		}
		
		// TODO Auto-generated method stub
		return list;
	}
}

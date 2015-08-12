package org.sitenv.xml.validators.ccda.structuredbody.coderequirements;

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.sitenv.xml.validators.ccda.CcdaValidatorResult;
import org.sitenv.xml.xpathvalidator.engine.XPathNodeValidator;
import org.sitenv.xml.xpathvalidator.engine.data.XPathValidatorResult;
import org.w3c.dom.Node;

public class MustBeValidator implements XPathNodeValidator {

	

	private static final Logger logger = Logger.getLogger(MustBeValidator.class);
	
	public XPathValidatorResult validateNode(String expression, XPath xpath, Node node, int nodeIndex, Map<String, String> params) {
		
		CcdaValidatorResult result = null;
		
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
			logger.trace("displayName: " + displayName);;
			
			String[] codeSystems = params.get("codeSystem").split("\\|");
			
			for (int i = 0; i < codeSystems.length; i++) 
			{
				if (codeSystem.equalsIgnoreCase(codeSystems[i]))
				{
					result = new CcdaValidatorResult();
					result.setRequestedCode(code);
					result.setRequestedCodeSystem(codeSystem);
					result.setRequestedCodeSystemName(codeSystemName);
					result.setRequestedDisplayName(displayName);
					result.setXpathExpression(expression);
					
					result.setNodeIndex(nodeIndex);
					result.setInformation(true);
					result.setInfoMessage("Code system '" + codeSystem + "' is valid for the node found at '" + expression + "[" + nodeIndex + "]'");
					
					return result;
				}
			}
			
			result = new CcdaValidatorResult();
			result.setRequestedCode(code);
			result.setRequestedCodeSystem(codeSystem);
			result.setRequestedCodeSystemName(codeSystemName);
			result.setRequestedDisplayName(displayName);
			result.setXpathExpression(expression);
			
			result.setNodeIndex(nodeIndex);
			result.setError(true);
			result.setErrorMessage("Code system '" + codeSystem + "' is not valid for the node found for '" + expression + "[" + nodeIndex + "]'");
		}
		catch (XPathExpressionException e)
		{
			logger.error(e);
		}
		
		
		
		return result;
	}

}

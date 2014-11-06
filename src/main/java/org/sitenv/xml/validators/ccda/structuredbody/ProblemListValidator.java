package org.sitenv.xml.validators.ccda.structuredbody;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.sitenv.xml.validators.ccda.CcdaCodeValidator;
import org.sitenv.xml.validators.ccda.CcdaValidatorResult;
import org.sitenv.xml.xpathvalidator.engine.MultipleXPathNodeValidator;
import org.sitenv.xml.xpathvalidator.engine.data.XPathValidatorResult;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProblemListValidator implements MultipleXPathNodeValidator {

	private static final Logger logger = Logger.getLogger(ProblemListValidator.class);
	private static final String[] EXPRESSIONS = {
		"code",
		"entry/act/code",
		"entry/act/entryRelationship/observation/code"
	};
	
	// this should be used with the template 2.16.840.1.113883.10.20.22.2.5.1
	public List<XPathValidatorResult> validateNode(String expression, XPath xpath, Node node, int nodeIndex) {
	
		List<XPathValidatorResult> results = null;
		 
		try {
			for (int count = 0; count < EXPRESSIONS.length; count++)
			{
				String localExp = EXPRESSIONS[count];
				XPathExpression expCode = xpath.compile(localExp);
				
				NodeList nodes = (NodeList) expCode.evaluate(node, XPathConstants.NODESET);
				
				for (int i = 0; i < nodes.getLength(); i++) {
					Node codeNode = nodes.item(i);
					
					CcdaCodeValidator codeValidator = new CcdaCodeValidator();
					CcdaValidatorResult result = codeValidator.validateNode(expression + "[" + nodeIndex + "]/" + localExp, xpath, codeNode, i);
					
					if (results == null)
					{
						results = new ArrayList<XPathValidatorResult>();
					}
					
					results.add(result);
				}
			}
			
		} catch (XPathExpressionException e) {
			logger.error(e);
		}
		
		logger.fatal("EXECUTED THE Problem List Validator");
		
		return results;
	}
}

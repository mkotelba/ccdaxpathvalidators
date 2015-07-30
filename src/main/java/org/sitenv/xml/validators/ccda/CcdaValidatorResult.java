package org.sitenv.xml.validators.ccda;

import java.util.Set;
import java.util.TreeSet;

import org.sitenv.xml.xpathvalidator.engine.data.XPathValidatorResult;

public class CcdaValidatorResult extends XPathValidatorResult {

	private String requestedCode;
	private String requestedCodeSystem;
	private String requestedCodeSystemName;
	private String requestedDisplayName;
	
	private Set<String> expectedValues = new TreeSet<String>();

	
	
	public String getRequestedCode() {
		return requestedCode;
	}

	public void setRequestedCode(String requestedCode) {
		this.requestedCode = requestedCode;
	}

	public String getRequestedCodeSystem() {
		return requestedCodeSystem;
	}

	public void setRequestedCodeSystem(String requestedCodeSystem) {
		this.requestedCodeSystem = requestedCodeSystem;
	}

	public String getRequestedCodeSystemName() {
		return requestedCodeSystemName;
	}

	public void setRequestedCodeSystemName(String requestedCodeSystemName) {
		this.requestedCodeSystemName = requestedCodeSystemName;
	}

	public String getRequestedDisplayName() {
		return requestedDisplayName;
	}

	public void setRequestedDisplayName(String requestedDisplayName) {
		this.requestedDisplayName = requestedDisplayName;
	}

	public Set<String> getExpectedValues() {
		return expectedValues;
	}

	public void setExpectedValues(Set<String> expectedValues) {
		this.expectedValues = expectedValues;
	}
	
	
	
}

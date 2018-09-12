/**
 * Copyright (C) 2009, Gajo Csaba
 *
 * This file is free software; the author gives unlimited 
 * permission to copy and/or distribute it, with or without
 * modifications, as long as this notice is preserved.
 * 
 * For more information read the LICENSE file that came
 * with this distribution. 
 */
package ai.keye.playground.makefile.objects;

import ai.keye.playground.makefile.managers.ErrorManager;
import ai.keye.playground.makefile.managers.VariableManager;

import java.io.Serializable;

import java.util.Stack;

/**
 * A Makefile variable/macro
 *
 */
public class Variable implements Serializable {

	private static final long serialVersionUID = -745362532720735619L;
	
	private String name = "";
	private String value;
	private boolean override = false;
	private boolean external = false;
	private boolean expanded = false;
	
	/** Empty constructor */
	public Variable() {}
	
	/** Create a variable with the specified name */
	public Variable(String name) {
		this.name = name;
	}
	
	/** Create a variable with the specified name and value */
	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Create a variable
	 * @param name		the name of the variable
	 * @param value		the value
	 * @param override	should this variable override other previous values?
	 * @param external	is this variable defined externally from the command line?
	 */
	public Variable(String name, String value, boolean override, boolean external) {
		this.name = name;
		this.value = value;
		this.override = override;
		this.external = external;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	/** Append value to value, same as += */
	public void append(String value) {
		if (value != null) {
			this.value += " " + value;
		}
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	/** Expand the macros in the variable */
	public Variable expand(VariableManager manager) {
		String vvv = getValue();
		System.out.flush();
		expanded = true;
		if (getValue() == null || getValue().trim().equals(""))
			return this;
		
		Stack<Integer> stack = new Stack<Integer>();
		boolean finished = false;
		int index = 0;
		int len = getValue().length();
		
		while (!finished) {
			if (index >= len) {
				finished = true;
			} else {
				char ch = getValue().charAt(index);
				/*// this section was removed because the Parser takes care of escape characters for us
				if (ch == '\\') {   // escape next character
					if (index+1 < len) {
						char ch2 = getValue().charAt(index+1);
						int start = index;
						String remd = "";
						if (index+2 < len) remd = getValue().substring(index+2);
						if (getValue().charAt(index+1) == '$') {   // \$ --> nothing
							setValue(getValue().substring(0,start) + remd);
							len--;  // another char less
							index--;
						} else {
							setValue(getValue().substring(0,start) + ch2 + remd);
						}
					}
					len--;  // one character less
					index++;
				} else */
				if (ch == '$') {
					if (index+1 < len && getValue().charAt(index+1) == '(') {   // $( macro start
						stack.push(Integer.valueOf(index+1));
						index += 2;
					} else if (index+1 < len && getValue().charAt(index+1) == '@') {
						index += 2;						
					} else {  // $$ == $, $ = nothing, delete it
						if (index+1 < len && getValue().charAt(index+1) == '$')
								index++;
						len--;
						int start = index;
						if (index + 1 < len) {
							String remd = getValue().substring(index + 1);
							setValue(getValue().substring(0, start) + remd);
						} else
							setValue(getValue().substring(0, start));
						len = getValue().length();   // new length
					}
				} else if (ch == ')' && !stack.isEmpty()) {
					Integer start = stack.pop();
					String eval = getValue().substring(start+1, index);
					
					if (eval.equals(getName())) {
						// eternal loop error
						if (ErrorManager.get().shouldFail("Circular loop found for $(" + getName() + ")", Variable.class)) {
							throw new Error("Circular loop found for $(" + getName() + ")");
						}
					}
					
					String remaind = "";
					if (index + 1 < len) remaind = getValue().substring(index+1);
					
					int initialLen = eval.length() + 2;
					int diff = eval.length() - initialLen;
					index += diff;
					
					eval = manager.getValue(eval);
					// System.out.println(eval);
					
					setValue(getValue().substring(0,start-1) + eval + remaind);
					len = getValue().length();   // new length
					
					//index = start - 1;
				} else {
					index++;
				}
			}
		}
		return this;
	}
	
	public String toString() {
		return "Variable [expanded: " + expanded + "] " + (isOverride()?"override ":"") + getName() + " = " + getValue();
	}
	
}

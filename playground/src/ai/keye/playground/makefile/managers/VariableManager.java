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
package ai.keye.playground.makefile.managers;

import ai.keye.playground.makefile.objects.Variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all the parsed variables
 *
 */
public class VariableManager {

	private Map<String,Variable> variables = new LinkedHashMap<String,Variable>();
	
	/** Default constructor; sets the pre-defined variables */
	public VariableManager() {
		this(true);
	}
	
	public VariableManager(boolean initialVariables) {
		super();
		if(initialVariables) {
			// applications
			addNew(new Variable("AR", "ar"));
			addNew(new Variable("AS", "as"));
			addNew(new Variable("CC", "cc"));
			addNew(new Variable("CO", "co"));
			addNew(new Variable("CXX", "g++"));
			addNew(new Variable("CPP", "$(CC) -g").expand(this));
			addNew(new Variable("FC", "f77"));
			addNew(new Variable("GET", "get"));
			addNew(new Variable("LEX", "lex"));
			addNew(new Variable("YACC", "yacc"));
			addNew(new Variable("LINT", "lint"));
			addNew(new Variable("M2C", "m2c"));
			addNew(new Variable("PC", "pc"));
			addNew(new Variable("MAKEINFO", "makeinfo"));
			addNew(new Variable("TEX", "tex"));
			addNew(new Variable("TEXI2DVI", "texi2dvi"));
			addNew(new Variable("WEAVE", "weave"));
			addNew(new Variable("CWEAVE", "cweave"));
			addNew(new Variable("TANGLE", "tangle"));
			addNew(new Variable("CTANGLE", "ctangle"));
			addNew(new Variable("RM", "rm -f"));
			// compiler flags
			addNew(new Variable("ARFLAGS", "rv"));
			addNew(new Variable("ASFLAGS", ""));
			addNew(new Variable("CFLAGS", ""));
			addNew(new Variable("CXXFLAGS", ""));
			addNew(new Variable("COFLAGS", ""));
			addNew(new Variable("CPPFLAGS", ""));
			addNew(new Variable("FFLAGS", ""));
			addNew(new Variable("GFLAGS", ""));
			addNew(new Variable("LDFLAGS", ""));
			addNew(new Variable("LFLAGS", ""));
			addNew(new Variable("YFLAGS", ""));
			addNew(new Variable("PFLAGS", ""));
			addNew(new Variable("RFLAGS", ""));
			addNew(new Variable("LINTFLAGS", ""));			
		}
	}


	public boolean isVarDefined(String id) {
		return variables.containsKey(id);
	}
	
	public String getValue(String id) {
		if (variables.containsKey(id)) {
			Variable var = variables.get(id);
			if (!var.isExpanded()) 
				var.expand(this);
			return var.getValue();
		}
		return "";
	}
	public String getUnexpandedValue(String id) {
            if (variables.containsKey(id)) {
                    Variable var = variables.get(id);                    
                    return var.getValue();
            }
            return "";
    }
	/**
	 * Add new variable. If the variable is already found, then
	 * it will be overridden. This will not happen only if the
	 * existing variable has override = true, or external = true.
	 * In this case, the variable will be overridden only if 
	 * itself has override = true. external = true does not have
	 * such effect, so only the first external variable will be
	 * recognized.
	 *  
	 * @param var	the variable
	 * @return	returns true if the variable was added
	 */
	public boolean addNew(Variable var) {
		if (variables.containsKey(var.getName())) {
			Variable existing = variables.get(var.getName());
			if (existing.isOverride() || existing.isExternal()) {
				if (var.isOverride()) {
					variables.remove(var.getName());  // remove previous
					variables.put(var.getName(), var);  // add new
					return true;
				}
			} else {
				variables.remove(var.getName());
				variables.put(var.getName(), var);
				return true;
			}
		} else {
			variables.remove(var.getName());
			variables.put(var.getName(), var);
			return true;
		}
		return false;
	}
	
	/** Append value to an existing variable */
	public void append(String id, String value) {
		if (variables.containsKey(id)) {
			Variable existing = variables.get(id);
			existing.append(value);
		} else {
			System.err.println("Warning: this should never happen! (VariableManager::append)");
		}
	}
	
	/** Get the names of all variables */
	public Iterator<String> keys() {
		return variables.keySet().iterator();
	}
	
	/** Get the names of all variables that are not external (= from command line) */
	public Iterator<String> nonExternalKeys() {
		List<String> res = new ArrayList<String>(variables.size());
		for (String id : variables.keySet()) {
			Variable var = variables.get(id);
			if (!var.isExternal()) {
				res.add(var.getName());
			}
		}
		return res.iterator();
	}
	
	/** Get the names of all variables whose value is not empty */
	public Iterator<String> nonNonEmptyKeys() {
		List<String> res = new ArrayList<String>(variables.size());
		for (String id : variables.keySet()) {
			Variable var = variables.get(id);
			if (var.getValue() != null && !var.getValue().equals("")) {
				res.add(var.getName());
			}
		}
		return res.iterator();
	}
	
	/** Get the current value of the variable */
	public Iterator<Variable> values() {
		return variables.values().iterator();
	}
	
	/** Expand a variable */
	public void expand(String varID) {
		if (variables.containsKey(varID)) {
			variables.get(varID).expand(this);
		}
	}
}

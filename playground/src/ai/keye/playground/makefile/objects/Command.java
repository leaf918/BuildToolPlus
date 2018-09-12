/**
 * Copyright (C) 2009, Gajo Csaba
 * <p>
 * This file is free software; the author gives unlimited
 * permission to copy and/or distribute it, with or without
 * modifications, as long as this notice is preserved.
 * <p>
 * For more information read the LICENSE file that came
 * with this distribution.
 */
package ai.keye.playground.makefile.objects;

import ai.keye.playground.makefile.managers.EnvManager;
import ai.keye.playground.makefile.managers.VariableManager;

import java.io.Serializable;
import java.util.Stack;

/**
 * Command to be executed directly
 */
public class Command implements Serializable {

    private static final long serialVersionUID = -7366249458746806274L;

    private String value;
    private boolean expanded = false;

    public Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void expandVariables(VariableManager manager) {
        expanded = true;
        if (getValue() == null)
            return;

        Stack<Integer> stack = new Stack<Integer>();
        boolean finished = false;
        int index = 0;
        int len = getValue().length();

        while (!finished) {
            if (index >= len) {
                finished = true;
            } else {
                char ch = getValue().charAt(index);
                if (ch == '\\') {   // escape next character
                    if (index + 1 < len) {
                        char ch2 = getValue().charAt(index + 1);
                        int start = index;
                        String remd = "";
                        if (index + 2 < len) remd = getValue().substring(index + 2);
                        setValue(getValue().substring(0, start) + ch2 + remd);
                    }
                    len--;  // one character less
                    index++;
                } else if (ch == '$') {
                    if (index + 1 < len && getValue().charAt(index + 1) == '(') {   // $( macro start
                        stack.push(Integer.valueOf(index + 1));
                        index += 2;
                    } else {  // $$ == $, $ = nothing, delete it
                        if (index + 1 < len && getValue().charAt(index + 1) == '$') {
                            index++;
                        }
                        len--;
                        int start = index;
                        String remd = "";
                        if (index + 1 < len) remd = getValue().substring(index + 1);
                        setValue(getValue().substring(0, start) + remd);
                    }
                } else if (ch == ')' && !stack.isEmpty()) {
                    Integer start = stack.pop();
                    String eval = getValue().substring(start + 1, index);

                    String remaind = "";
                    if (index + 1 < len) remaind = getValue().substring(index + 1);

                    int initialLen = eval.length() + 2;
                    int diff = eval.length() - initialLen;
                    //index += diff;

                    eval = manager.getValue(eval);

                    setValue(getValue().substring(0, start - 1) + eval + remaind);
                    len = getValue().length();   // new length

                    index = start - 1;

                } else if (ch == '~') {  // replace with $HOME
                    int start = index;
                    String remd = "";
                    if (index + 1 < len) remd = getValue().substring(index + 1);

                    String homeDir = EnvManager.get().getProperty("HOME");
                    if (homeDir == null) homeDir = "";
                    else {
                        len += homeDir.length() - 1;
                        index += homeDir.length();
                    }

                    setValue(getValue().substring(0, start) + homeDir + remd);
                } else {
                    index++;
                }
            }
        }
    }

    public String toString() {
        return "Command [expanded: " + expanded + "] " + getValue();
    }

}

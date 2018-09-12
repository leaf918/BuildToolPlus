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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Makefile target
 */
public class Target implements Serializable {

    private static final long serialVersionUID = 6715778047779711769L;

    private String name = "";
    private boolean uniqueName = true;
    private List<String> normalPrerequisites = new ArrayList<String>();
    private List<String> orderOnlyPrerequisites = new ArrayList<String>();
    private List<Command> commands = new ArrayList<Command>();

    public Target(String name) {
        this.name = name;
    }

    public Target(String name, boolean uniqueName) {
        this.name = name;
        this.uniqueName = uniqueName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public boolean isUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(boolean uniqueName) {
        this.uniqueName = uniqueName;
    }

    public List<String> getNormalPrerequisites() {
        return normalPrerequisites;
    }

    public void setNormalPrerequisites(List<String> normalPrerequisites) {
        if (normalPrerequisites != null) {
            this.normalPrerequisites = normalPrerequisites;
        }
    }

    public List<String> getOrderOnlyPrerequisites() {
        return orderOnlyPrerequisites;
    }

    public void setOrderOnlyPrerequisites(List<String> orderOnlyPrerequisites) {
        if (orderOnlyPrerequisites != null) {
            this.orderOnlyPrerequisites = orderOnlyPrerequisites;
        }
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        if (commands != null) {
            this.commands = commands;
        }
    }

    public String toString() {
        StringBuffer res = new StringBuffer(256);
        res.append("=====\nTarget ").append(getName()).append(" : ");
        for (String preq : normalPrerequisites) {
            res.append(preq).append(' ');
        }
        if (!orderOnlyPrerequisites.isEmpty()) {
            res.append("| ");
            for (String preq : orderOnlyPrerequisites) {
                res.append(preq).append(' ');
            }
        }
        if (!commands.isEmpty()) {
            res.append('\n');
            for (Command comm : commands) {
                res.append('\t').append(comm.toString()).append('\n');
            }
        }
        return res.toString();
    }

}

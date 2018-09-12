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
package ai.keye.playground.makefile;

import ai.keye.playground.makefile.managers.VariableManager;
import ai.keye.playground.makefile.objects.Variable;

import java.io.File;


/**
 * The main class of the application
 */
public class MakefileParseMain {

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        VariableManager manager = new VariableManager(false);
        // load external variables
        if (args != null && args.length > 0) {
            String fileName = args[0];
            for (int i = 1; i < args.length; i++) {
                String[] var = args[i].split("=");
                if (var.length == 2) {
                    manager.addNew(new Variable(var[0], var[1], false, true));
                }
            }

            try {
                File file = new File(fileName);
                if (!file.exists()) {
                    System.out.println("The file " + fileName + " doesn't exists.");
                    System.exit(2);
                } else if (file.isDirectory()) {
                    System.out.println("The file " + fileName + " is a directory");
                    System.exit(3);
                } else {
                    System.out.println("Using " + extractDir(file) + " as the work directory.");
                    System.setProperty("user.dir", extractDir(file));

                    // start parsing
                    Parser parser = new Parser(manager);
                    parser.parse(file);
                    System.out.println(parser);
                }
            } catch (Throwable t) {
                System.err.println("An error occured while attempting to parse the file.");
                t.printStackTrace();
                System.exit(1);
            }

        } else {
            System.out.println("No Makefiles defined.");
        }
    }

    /**
     * Extract the absolute path to the file's directory
     */
    protected static String extractDir(File file) {
        String path = file.getAbsolutePath();
        int index = path.lastIndexOf(File.separator);
        if (index == path.length() - 1) {
            index = path.lastIndexOf(File.separator, path.length() - 2);
        }
        return path.substring(0, index);
    }

}

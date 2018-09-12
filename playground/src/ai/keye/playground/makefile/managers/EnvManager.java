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
package ai.keye.playground.makefile.managers;

import java.util.Properties;

/**
 * Environment variables
 */
public class EnvManager {

    private Properties props = new Properties();

    // hidden constructor
    private EnvManager() {
        // load environment properties
        for (String key : System.getenv().keySet()) {
            props.put(key, System.getenv(key));
        }
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    private static EnvManager instance;

    /**
     * Get instance of Envmanager
     */
    public static EnvManager get() {
        if (instance == null) {
            instance = new EnvManager();
        }
        return instance;
    }
}

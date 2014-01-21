/*
 * Copyright (C) 2013 Spencer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogue.playtime2.config;

/**
 * Holds string instances of config values
 *
 * @since 2.0.0
 * @author 1Rogue
 * @version 2.0.0
 */
public enum ConfigValues {

    DEBUG_LEVEL("general.debug-level", 0),
    UPDATE_CHECK("update.check", true),
    UPDATE_DOWNLOAD("update.download", true);
    
    private final String key;
    private final Object def;

    /**
     * private constructor for {@link ConfigValues}
     *
     * @since 2.0.0
     * @version 2.0.0
     *
     * @param key The string path to the value
     */
    private ConfigValues(String key, Object def) {
        this.key = key;
        this.def = def;
    }

    /**
     * Returns the default value and key path in the format:
     * <br>    default-value@key-path-value
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @return The default and key-path value of the {@link ConfigValues} variable
     */
    @Override
    public String toString() {
        return this.def + "@" + this.key;
    }
    
    /**
     * Returns the string path to the value
     *
     * @since 2.0.0
     * @version 2.0.0
     *
     * @return The string path to the value
     */
    public String getPath() {
        return this.key;
    }
    
    /**
     * Returns the default value of the key
     *
     * @since 2.0.0
     * @version 2.0.0
     *
     * @return The key's default value
     */
    public Object getDefault() {
        return this.def;
    }
}

/*
 * This file is part of Dynamic Rules.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This file is part of SmartStreets.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.jcorp.dynrules.sys;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author artamonov
 */
public class Utf8ResourceBundle {

    private Utf8ResourceBundle() {
    }

    public static ResourceBundle getBundle(String baseName) {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName);
        return createUtf8PropertyResourceBundle(bundle);
    }

    private static ResourceBundle createUtf8PropertyResourceBundle(ResourceBundle bundle) {
        if (!(bundle instanceof PropertyResourceBundle)) return bundle;

        return new Utf8PropertyResourceBundle((PropertyResourceBundle) bundle);
    }

    private static class Utf8PropertyResourceBundle extends ResourceBundle {
        PropertyResourceBundle bundle;

        private Utf8PropertyResourceBundle(PropertyResourceBundle bundle) {
            this.bundle = bundle;
        }

        @Override
        public Enumeration<String> getKeys() {
            return bundle.getKeys();
        }

        @Override
        protected Object handleGetObject(String key) {
            String value = (String) bundle.handleGetObject(key);
            try {
                if (StringUtils.isNotEmpty(value))
                    return new String(value.getBytes("ISO-8859-1"), "UTF-8");
                else
                    return "";
            } catch (UnsupportedEncodingException e) {
                // Shouldn't fail - but should we still add logging message?
                return null;
            }
        }
    }
}
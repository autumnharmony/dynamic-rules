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

package ru.jcorp.dynrules

import javax.swing.SwingUtilities
import javax.swing.UIManager
import ru.jcorp.dynrules.gui.MainWindow
import groovy.swing.SwingBuilder
import ru.jcorp.dynrules.sys.Utf8ResourceBundle
import javax.swing.ImageIcon
import java.awt.Image

/**
 * @author artamonov
 */
class DynamicRulesApp {

    private MainWindow mainWindow;

    private SwingBuilder guiBuilder;

    private ResourceBundle resourceBundle;

    private static DynamicRulesApp instance;

    DynamicRulesApp() {
        // load localization
        resourceBundle = Utf8ResourceBundle.getBundle('locale.messages')

        // create GUI factory
        guiBuilder = new SwingBuilder()
    }

    private void run() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel('com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel')
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                mainWindow = new MainWindow();
                mainWindow.setLocationByPlatform(true);
                mainWindow.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        instance = new DynamicRulesApp()
        instance.run()
    }

    MainWindow getMainWindow() {
        return mainWindow
    }

    SwingBuilder getGuiBuilder() {
        return guiBuilder
    }

    ResourceBundle getResourceBundle() {
        return resourceBundle
    }

    static DynamicRulesApp getInstance() {
        return instance
    }

    String getMessage(String key) {
        return resourceBundle.getString(key)
    }

    ImageIcon getResourceIcon(String name) {
        return new ImageIcon(DynamicRulesApp.class.getResource('/icons/' + name))
    }

    Image getResourceImage(String name) {
        return getResourceIcon(name).getImage()
    }
}
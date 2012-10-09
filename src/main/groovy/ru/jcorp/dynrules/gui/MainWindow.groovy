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

package ru.jcorp.dynrules.gui

import javax.swing.JFrame
import ru.jcorp.dynrules.DynamicRulesApp

/**
 * @author artamonov
 */
class MainWindow extends JFrame {

    private DynamicRulesApp app

    MainWindow() {
        this.app = DynamicRulesApp.instance

        this.size = [640, 480]
        this.minimumSize = [640, 480]
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.title = app.getMessage('application.title')
        this.iconImage = app.getResourceImage('application.png')

        buildMenu()
    }

    def buildMenu() {
        def menuBar = app.guiBuilder.menuBar() {
            menu(text: app.getMessage('menu.file')) {
                menuItem(text: app.getMessage('menu.file.new'), //icon: app.getResourceIcon('menu/map.png'),
                        actionPerformed: {
                            newRules()
                        })
                menuItem(text: app.getMessage('menu.file.open'), //icon: app.getResourceIcon('menu/open.png'),
                        actionPerformed: {
                            selectRules()
                        })
                menuItem(text: app.getMessage('menu.file.save'), //icon: app.getResourceIcon('menu/save.png'),
                        actionPerformed: {
                            saveRules()
                        })
                separator()
                menuItem(text: app.getMessage('menu.file.exit'), //icon: app.getResourceIcon('menu/exit.png'),
                        actionPerformed: { System.exit(0) })
            }
            menu(text: app.getMessage('menu.help')) {
                menuItem(text: app.getMessage('menu.help.about'), //icon: app.getResourceIcon('menu/about.png'),
                        actionPerformed: {
                            AboutWindow about = new AboutWindow()
                            about.locationRelativeTo = this
                            about.setVisible(true)
                        })
            }
        }
        setJMenuBar(menuBar)
    }

    def newRules(){}

    def selectRules(){}

    def saveRules(){}

}
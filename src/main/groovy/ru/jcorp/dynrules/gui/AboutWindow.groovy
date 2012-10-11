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

import ru.jcorp.dynrules.DynamicRulesApp

import java.awt.BorderLayout
import javax.swing.JDialog
import javax.swing.border.EmptyBorder

/**
 * @author artamonov
 */
class AboutWindow extends JDialog {

    private DynamicRulesApp app

    AboutWindow() {
        this.app = DynamicRulesApp.instance

        this.size = [500, 350]
        this.title = app.getMessage('menu.help.about')
        this.iconImage = app.getResourceImage('application.png')

        buildContentPane()
    }

    def buildContentPane() {
        def panel = app.guiBuilder.panel(constraints: BorderLayout.CENTER) {
            borderLayout()
            vbox(constraints: PAGE_START, border: new EmptyBorder(3, 5, 3, 5)) {
                label(text: app.getMessage('authors'), border: new EmptyBorder(3, 5, 3, 5))
                label(text: app.getMessage('teacher'), border: new EmptyBorder(3, 5, 3, 5))
            }
            textArea(constraints: CENTER, text: readTask(), editable: false)
            hbox(constraints: PAGE_END, border: new EmptyBorder(3, 5, 3, 5)) {
                hglue()
                button(text: app.getMessage('edit.ok'), actionPerformed: { this.dispose() })
                hglue()
            }
        }
        add(panel)
    }

    def readTask() {
        InputStream problemStream = getClass().getResourceAsStream('/TASK')
        String taskText = problemStream.withReader {
            reader ->
            return reader.readLines().join('\n')
        }
        return taskText
    }
}
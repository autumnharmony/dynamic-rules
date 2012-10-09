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
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

/**
 * @author artamonov
 */
class AboutWindow extends JDialog {

    private DynamicRulesApp app

    AboutWindow() {
        this.app = DynamicRulesApp.getInstance()

        this.title = app.getMessage('menu.help.about')
        this.size = new Dimension(500, 350)

        buildContentPane();
    }

    def buildContentPane() {
        def panel = app.guiBuilder.panel(constraints: BorderLayout.CENTER) {
            borderLayout()
            hbox(constraints: PAGE_START, border: new EmptyBorder(3, 5, 3, 5), preferredSize: [-1, 56]) {
                widget(new JPanel() {
                    def image = app.getResourceImage('application.png')

                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g)
                        g.drawImage(image, 0, 0, null);
                    }
                }, border: new EmptyBorder(3, 5, 3, 5))

                vbox {
                    label(text: app.getMessage('authors'),
                            border: new EmptyBorder(3, 5, 3, 5))
                    label(text: app.getMessage('teacher'),
                            border: new EmptyBorder(3, 5, 3, 5))
                    vglue()
                }
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

    private def readTask() {
        InputStream problemStream = getClass().getResourceAsStream("/TASK");
        BufferedReader reader = new BufferedReader(new InputStreamReader(problemStream, "UTF-8"));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
            builder.append(line).append("\n");

        return builder.toString()
    }
}
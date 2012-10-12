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

package ru.jcorp.dynrules.gui.controls

import ru.jcorp.dynrules.DynamicRulesApp

import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * @author artamonov
 */
class SelectInputControl implements InputControl<Integer> {

    private String message
    private List<Enum> values

    private JPanel composition
    private DynamicRulesApp app

    SelectInputControl(String message, List<Enum> values) {
        this.message = message
        this.values = values
        this.app = DynamicRulesApp.instance

        def modelItems = values.toArray(new Enum[values.size()])
        def itemsModel = new DefaultComboBoxModel<Enum>(modelItems)
        composition = app.guiBuilder.panel() {
            hbox {
                label(value: message)
                comboBox(model: itemsModel)
                button(text: app.getMessage('edit.next'), actionPerformed: {

                })
                button(text: app.getMessage('edit.unknown'), actionPerformed: {

                })
            }
        }
    }

    @Override
    Integer getValue() {
        return null
    }

    @Override
    JComponent getComposition() {
        return composition
    }
}
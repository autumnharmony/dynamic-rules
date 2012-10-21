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

import javax.swing.Action
import javax.swing.AbstractAction
import java.awt.event.ActionEvent
import javax.swing.JComboBox

/**
 * @author artamonov
 */
class SelectInputControl<T extends Enum> implements InputControl<T> {

    private Collection<T> values

    private JComboBox component

    private Action nextAction

    private DynamicRulesApp app

    private volatile Object value = null

    SelectInputControl(Collection<T> values) {
        this.app = DynamicRulesApp.instance
        this.values = values

        this.nextAction = new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent e) {
                value = component.getSelectedItem()
            }
        }

        def modelItems = values.toArray(new Enum[values.size()])
        def itemsModel = new DefaultComboBoxModel<Enum>(modelItems)

        this.component = app.guiBuilder.comboBox(id: 'resultBox', model: itemsModel,
                size: [150, -1], minimumSize: [150, -1], preferredSize: [150, -1])
    }

    @Override
    T getValue() {
        return (T) value
    }

    @Override
    JComponent getComponent() {
        return component
    }

    @Override
    Action getNextAction() {
        return nextAction
    }
}
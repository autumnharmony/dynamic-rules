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

import javax.swing.JComponent
import ru.jcorp.dynrules.DynamicRulesApp

import javax.swing.Action
import javax.swing.JTextField
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import ru.jcorp.dynrules.exceptions.ValidationException
import java.awt.event.KeyListener
import java.awt.event.KeyEvent
import javax.swing.JOptionPane
import java.awt.event.KeyAdapter

/**
 * @author artamonov
 */
class NumberInputControl implements InputControl<Double> {

    private DynamicRulesApp app

    private JTextField component

    private Action nextAction

    private volatile Double value = null

    NumberInputControl() {
        this.app = DynamicRulesApp.instance

        this.nextAction = new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent e) {
                try {
                    apply()
                } catch (NumberFormatException ignored) {
                    throw new ValidationException()
                }
            }
        }

        component = app.guiBuilder.textField(text: '0',
                size: [150, -1], minimumSize: [150, -1], preferredSize: [150, -1])
        component.addKeyListener(new KeyAdapter() {
            @Override
            void keyPressed(KeyEvent e) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    try {
                        apply()
                    } catch (ValidationException ignored) {
                        def app = DynamicRulesApp.instance
                        JOptionPane.showMessageDialog(component,
                                app.getMessage('edit.validation'),
                                app.getMessage('edit.warning'),
                                JOptionPane.WARNING_MESSAGE)
                        return
                    }

                    component.enabled = false

                    synchronized (NumberInputControl.this) {
                        NumberInputControl.this.notifyAll()
                    }
                }
            }
        })
    }

    def apply() {
        try {
            value = Double.parseDouble(component.text)
        } catch (NumberFormatException ignored) {
            throw new ValidationException()
        }
    }

    @Override
    def Double getValue() {
        return value
    }

    @Override
    JComponent getComponent() {
        return component
    }

    @Override
    Action getNextAction() {
        return nextAction
    }

    @Override
    void clear() {
        component.text = ''
    }
}
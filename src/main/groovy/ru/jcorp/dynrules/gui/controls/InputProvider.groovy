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

import java.awt.Dimension
import java.awt.EventQueue
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * @author artamonov
 */
class InputProvider {

    private JPanel dialogPane
    private JButton nextButton
    private JButton unknownBtn

    InputProvider(JPanel dialogPane, JButton nextButton, JButton unknownBtn) {
        this.dialogPane = dialogPane
        this.nextButton = nextButton
        this.unknownBtn = unknownBtn
    }

    def <T> T showInputControl(InputControl<T> inputControl, String messageCode) {
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            void run() {
                dialogPane.add(new JLabel(DynamicRulesApp.instance.getMessage(messageCode)))
                dialogPane.add(inputControl.component)

                nextButton.setAction(new AbstractAction(nextButton.text) {
                    @Override
                    void actionPerformed(ActionEvent e) {
                        inputControl.nextAction.actionPerformed(e)
                        inputControl.component.enabled = false

                        synchronized (inputControl) {
                            inputControl.notifyAll()
                        }
                    }
                })
                unknownBtn.setAction(new AbstractAction(unknownBtn.text) {

                    @Override
                    void actionPerformed(ActionEvent e) {
                        inputControl.component.enabled = false

                        synchronized (inputControl) {
                            inputControl.notifyAll()
                        }
                    }
                })

                inputControl.component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30))
                inputControl.component.setPreferredSize(new Dimension(-1, 30))
            }
        })
        dialogPane.getRootPane().repaint()
        synchronized (inputControl) {
            inputControl.wait()
        }
        return inputControl.value
    }

    void printResult(result) {
        nextButton.visible = false
        unknownBtn.visible = false
        // todo print result here
    }

    void printUnresolvedSystemMessage() {
        nextButton.visible = false
        unknownBtn.visible = false
        // todo print unresolved message
    }
}
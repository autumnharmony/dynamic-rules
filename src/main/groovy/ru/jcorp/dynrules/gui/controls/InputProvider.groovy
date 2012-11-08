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
import ru.jcorp.dynrules.exceptions.ValidationException
import javax.swing.JOptionPane
import javax.swing.JList
import javax.swing.border.LineBorder
import java.awt.Color

/**
 * @author artamonov
 */
class InputProvider {

    private JPanel dialogPane
    private JButton nextButton
    private JButton reasonBtn
    private JButton unknownBtn
    private JPanel resultContainer

    InputProvider(JPanel dialogPane, JButton nextButton, JButton unknownBtn, JButton reasonBtn, JPanel resultContainer) {
        this.dialogPane = dialogPane
        this.nextButton = nextButton
        this.reasonBtn = reasonBtn
        this.unknownBtn = unknownBtn
        this.resultContainer = resultContainer
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
                        try {
                            inputControl.nextAction.actionPerformed(e)
                        } catch (ValidationException ignored) {
                            def app = DynamicRulesApp.instance
                            JOptionPane.showMessageDialog(inputControl.component,
                                    app.getMessage('edit.validation'),
                                    app.getMessage('edit.warning'),
                                    JOptionPane.WARNING_MESSAGE)
                            return
                        }

                        inputControl.component.enabled = false

                        synchronized (inputControl) {
                            inputControl.notifyAll()
                        }
                    }
                })
                unknownBtn.setAction(new AbstractAction(unknownBtn.text) {

                    @Override
                    void actionPerformed(ActionEvent e) {
                        inputControl.clear()
                        inputControl.component.enabled = false

                        synchronized (inputControl) {
                            inputControl.notifyAll()
                        }
                    }
                })

                inputControl.component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30))
                inputControl.component.setPreferredSize(new Dimension(-1, 30))
                inputControl.component.requestFocus()
            }
        })
        dialogPane.getRootPane()?.repaint()
        synchronized (inputControl) {
            inputControl.wait()
        }
        return inputControl.value
    }

    void printResult(result) {
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            void run() {
                nextButton.visible = false
                unknownBtn.visible = false
                if (result != null) {
                    JList resultList = new JList((result as Collection).toArray())
                    resultList.setBorder(new LineBorder(Color.BLACK))
                    resultContainer.add(resultList)
                    reasonBtn.visible = true
                } else {
                    JLabel resultLabel = new JLabel()
                    resultLabel.setText(DynamicRulesApp.instance.getMessage('result.unresolved'))
                    resultContainer.add(resultLabel)
                }
            }
        })
        dialogPane.getRootPane()?.repaint()
    }

    void printUnresolvedSystemMessage() {
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            void run() {
                JLabel resultLabel = new JLabel()
                nextButton.visible = false
                unknownBtn.visible = false
                resultLabel.setText(DynamicRulesApp.instance.getMessage('result.unresolved'))
                resultContainer.add(resultLabel)
            }
        })
        dialogPane.getRootPane()?.repaint()
    }

    void showProductionError(Exception e) {
        e.printStackTrace()
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            void run() {
                JOptionPane.showMessageDialog(dialogPane,
                        DynamicRulesApp.instance.getMessage('edit.productionError'),
                        DynamicRulesApp.instance.getMessage('edit.error'),
                        JOptionPane.ERROR_MESSAGE)
            }
        })
    }
}
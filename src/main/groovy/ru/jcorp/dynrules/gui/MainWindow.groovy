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
import ru.jcorp.dynrules.gui.controls.InputProvider
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.production.Executor
import ru.jcorp.dynrules.production.ProductionMethod
import ru.jcorp.dynrules.production.impl.DirectProduction
import ru.jcorp.dynrules.util.DslSupport

import javax.swing.border.EmptyBorder
import javax.swing.*
import ru.jcorp.dynrules.domain.impl.DirectDomainObject
import ru.jcorp.dynrules.domain.impl.InvertedDomainObject
import ru.jcorp.dynrules.production.impl.InvertedProduction
import java.awt.event.ActionEvent

/**
 * @author artamonov
 */
class MainWindow extends JFrame {

    private DynamicRulesApp app
    private JTabbedPane tabbedPane

    private Map<Long, Executor> executorMap = new WeakHashMap<Long, Executor>()

    MainWindow() {
        this.app = DynamicRulesApp.instance

        this.size = [640, 480]
        this.minimumSize = [480, 320]
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.title = app.getMessage('application.title')
        this.iconImage = app.getResourceImage('application.png')

        buildMenu()
        buildContentPane()
    }

    def buildMenu() {
        def menuBar = app.guiBuilder.menuBar() {
            menu(text: app.getMessage('menu.file')) {
                menuItem(text: app.getMessage('menu.file.new'), icon: app.getResourceIcon('menu/lightning.png'),
                        actionPerformed: {
                            newConsultation()
                        })
                menuItem(text: app.getMessage('menu.file.loadRules'), icon: app.getResourceIcon('menu/open.png'),
                        actionPerformed: {
                            selectRules()
                        })
                separator()
                menuItem(text: app.getMessage('menu.file.exit'), icon: app.getResourceIcon('menu/exit.png'),
                        actionPerformed: { System.exit(0) })
            }
            menu(text: app.getMessage('menu.help')) {
                menuItem(text: app.getMessage('menu.help.about'), icon: app.getResourceIcon('menu/about.png'),
                        actionPerformed: {
                            AboutWindow about = new AboutWindow()
                            about.locationRelativeTo = this
                            about.visible = true
                        })
            }
        }
        setJMenuBar(menuBar)
    }

    def buildContentPane() {
        def contentPane = app.guiBuilder.panel() {
            borderLayout()
            tabbedPane = tabbedPane(constraints: CENTER) {
            }
        }
        setContentPane(contentPane)
    }

    def newConsultation() {
        final Long execNumber = new Date().time

        JPanel dialogPane = null
        JButton nextBtn = null
        JButton unknownBtn = null
        JButton reasonBtn = null
        JPanel resultContainer = null
        def consultationPanel = app.guiBuilder.panel(border: new EmptyBorder(3, 5, 3, 5)) {
            borderLayout()

            hbox(constraints: PAGE_START) {
                hglue()
                dialogPane = panel {
                    gridLayout(columns: 2, rows: -1)
                }
                hglue()
            }

            hbox(constraints: CENTER) {
                hglue()
                resultContainer = panel() {
                    boxLayout(axis: BoxLayout.Y_AXIS)
                }
                hglue()
            }

            hbox(constraints: PAGE_END) {
                nextBtn = button(text: app.getMessage('edit.next'))
                unknownBtn = button(text: app.getMessage('edit.unknown'))
                reasonBtn = button(text: app.getMessage('edit.reason'), visible: false)

                hglue()

                button(text: app.getMessage('edit.finish'), actionPerformed: {
                    executorMap.get(execNumber)?.cancel()
                    // stop consulation
                    tabbedPane.remove(tabbedPane.selectedComponent)
                })
            }
        }
        InputProvider inputProvider = new InputProvider(dialogPane, nextBtn, unknownBtn, reasonBtn, resultContainer)

        def options = [app.getMessage('production.direct'), app.getMessage('production.inverted')]
        def option = JOptionPane.showOptionDialog(this,
                app.getMessage('edit.selectProduction'),
                app.getMessage('edit.options'), JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null,
                options.toArray(), "");

        ProductionMethod method
        DomainObject domainObject

        if (option == 0) {
            domainObject = new DirectDomainObject(inputProvider)
            method = new DirectProduction(domainObject)
        } else {
            Stack<String> vars = new Stack<String>()
            domainObject = new InvertedDomainObject(inputProvider, vars)
            method = new InvertedProduction(domainObject, vars)
        }

        reasonBtn.setAction(new AbstractAction(reasonBtn.text) {

            @Override
            void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainWindow.this, domainObject.reason, app.getMessage("edit.reason"), JOptionPane.INFORMATION_MESSAGE)
            }
        })

        RuleSet ruleSet = RuleSet.build DslSupport.loadClosureFromResource('/rules/set.groovy')

        Executor executor = new Executor(method, inputProvider, ruleSet)
        executor.performProduction()

        executorMap.put(execNumber, executor)

        tabbedPane.add(
                String.format(app.getMessage('consultation.title'), tabbedPane.tabCount + 1, options.get(option))
                , consultationPanel)
        tabbedPane.selectedComponent = consultationPanel
    }

    def selectRules() {

    }
}
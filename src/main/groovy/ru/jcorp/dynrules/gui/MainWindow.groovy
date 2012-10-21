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
import ru.jcorp.dynrules.domain.impl.DirectDomainObject
import ru.jcorp.dynrules.gui.controls.InputProvider
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.production.Executor
import ru.jcorp.dynrules.production.ProductionMethod
import ru.jcorp.dynrules.production.impl.DirectProduction
import ru.jcorp.dynrules.util.DslSupport

import javax.swing.JFrame
import javax.swing.JTabbedPane
import javax.swing.border.EmptyBorder
import javax.swing.JPanel
import javax.swing.BoxLayout
import javax.swing.JButton

/**
 * @author artamonov
 */
class MainWindow extends JFrame {

    private DynamicRulesApp app
    private JTabbedPane tabbedPane

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
        JPanel dialogPane = null
        JButton nextBtn = null
        JButton unknownBtn = null
        def consultationPanel = app.guiBuilder.panel(border: new EmptyBorder(3, 5, 3, 5)) {
            boxLayout(axis: BoxLayout.Y_AXIS)

            dialogPane = panel(maximumSize: [300, 0]) {
                gridLayout(columns: 2, rows: -1)
            }

            vglue()

            hbox() {
                nextBtn = button(text: app.getMessage('edit.next'))
                unknownBtn = button(text: app.getMessage('edit.unknown'))

                hglue()

                button(text: app.getMessage('edit.finish'), actionPerformed: {
                    // stop consulation
                    tabbedPane.remove(tabbedPane.selectedComponent)
                })
            }
        }
        InputProvider inputProvider = new InputProvider(dialogPane, nextBtn, unknownBtn)
        DomainObject domainObject = new DirectDomainObject(inputProvider)
        ProductionMethod directMethod = new DirectProduction(domainObject)

        RuleSet ruleSet = RuleSet.build DslSupport.loadClosureFromResource('/rules/set.groovy')

        Executor executor = new Executor(directMethod, inputProvider, ruleSet)
        executor.performProduction()

        tabbedPane.add(String.format(app.getMessage('consultation.title'), tabbedPane.tabCount + 1), consultationPanel)
        tabbedPane.selectedComponent = consultationPanel
    }

    def selectRules() {}
}
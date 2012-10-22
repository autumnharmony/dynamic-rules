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

package ru.jcorp.dynrules.domain.impl

import ru.jcorp.dynrules.domain.BasicDomainObject
import ru.jcorp.dynrules.gui.controls.InputProvider
import ru.jcorp.dynrules.domain.CraneType

/**
 * @author artamonov
 */
class InvertedDomainObject extends BasicDomainObject {

    private Stack<String> variablesStack

    InvertedDomainObject(InputProvider inputProvider, Stack<String> variablesStack) {
        super(inputProvider)

        this.variablesStack = variablesStack
        this.variablesStack.add('RESULT')
    }

    def setResult(List<CraneType> result) {
        this._RESULT_ = result
        this.variablesStack.remove('RESULT')
        this.inputProvider.printResult(result)
    }
}
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

package ru.jcorp.dynrules.domain

import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.exceptions.CannotInputVariableException

/**
 * @author ulbekov
 */
class InvertedTestDomainObject extends GroovyObjectSupport implements DomainObject {

    private Integer _X_ = null

    private Integer _T_ = null

    private Integer _C_ = null

    private List<String> RESULT = null

    static final String A = 'AX'

    static final String B = 'BX'

    private Scanner reader

    private PrintWriter printer

    private Stack<String> variablesStack

    InvertedTestDomainObject(Reader reader, PrintWriter printer, Stack<String> variablesStack) {
        this.reader = new Scanner(reader)
        this.printer = printer
        this.variablesStack = variablesStack
        this.variablesStack.add('RESULT')
    }

    Integer getX() {
        if (!_X_) {
            if (printer) {
                printer.println 'Please input X:'
                printer.flush()
            }
            _X_ = reader.nextInt()
        }
        return _X_
    }

    Integer getC() {
        if (!_C_) {
            variablesStack.add('C')
            throw new CannotInputVariableException('C');
        }
        return _C_
    }

    void setC(Integer C) {
        variablesStack.remove('C')
        this._C_ = C
    }

    Integer getT() {
        if (!_T_) {
            if (printer) {
                printer.println 'Please input T:'
                printer.flush()
            }
            _T_ = reader.nextInt()
        }
        return _T_
    }

    List<String> getRESULT() {
        return RESULT
    }

    void setRESULT(List<String> result) {
        variablesStack.remove('RESULT')
        RESULT = result
    }

    @Override
    Object getProperty(String property) {
        if ('X'.equals(property))
            return getX()

        if ('T'.equals(property))
            return getT()

        if ('C'.equals(property))
            return getC()

        if ('RESULT'.equals(property))
            return getRESULT()

        return super.getProperty(property)
    }

    @Override
    void setProperty(String property, Object newValue) {
        if ('C'.equals(property))
            setC((Integer) newValue)
        else if ('RESULT'.equals(property))
            setRESULT((List<String>) newValue)
        else
            super.setProperty(property, newValue)
    }

    @Override
    boolean isResolved() {
        return RESULT != null
    }

    @Override
    void setReason(String reason) {
    }

    @Override
    String getReason() {
        return null
    }
}

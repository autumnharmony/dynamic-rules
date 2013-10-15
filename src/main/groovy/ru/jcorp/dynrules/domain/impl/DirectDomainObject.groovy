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
import ru.jcorp.dynrules.domain.CraneType
import ru.jcorp.dynrules.gui.controls.InputProvider
import org.apache.commons.lang.StringUtils
import ru.jcorp.dynrules.exceptions.CannotInputVariableException

/**
 * @author artamonov
 */
class DirectDomainObject extends BasicDomainObject {

    DirectDomainObject(InputProvider inputProvider) {
        super(inputProvider)
    }

    def setResult(List<CraneType> result) {
        for(CraneType t : result) {
            if(_RESULT_.indexOf(t) == -1) {
                _RESULT_.add(t)
            }
        }
        this.inputProvider.printResult(_RESULT_)
    }

    @Override
    Object getProperty(String property) {
        if (hasProperty(property))
            return super.getProperty(property)
        else {
            String lcProperty = StringUtils.lowerCase(property)
            if (hasProperty(lcProperty) && !StringUtils.equals(property, lcProperty))
                return super.getProperty(lcProperty)
            else if (miscVariables.containsKey(property))
                return miscVariables.get(property)
            else
                throw new CannotInputVariableException(property)
        }
    }

    @Override
    void setProperty(String property, Object newValue) {
        if (hasProperty(property))
            super.setProperty(property, newValue)
        else {
            String lcProperty = StringUtils.lowerCase(property)
            if (hasProperty(lcProperty) && !StringUtils.equals(property, lcProperty))
                super.setProperty(lcProperty, newValue)
            else
                miscVariables.put(property, newValue)
        }
    }
}
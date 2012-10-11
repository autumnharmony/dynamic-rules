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
import ru.jcorp.dynrules.exceptions.CannotReassignVariableException

/**
 * @author artamonov
 */
abstract class BasicDomainObject extends GroovyObjectSupport implements DomainObject {

    protected Integer _G_ = null
    protected Integer _O_ = null
    protected Integer _V_ = null
    protected Integer _H_ = null

    final CraneType MK = CraneType.MK
    final CraneType NKPK = CraneType.NKPK
    final CraneType KK = CraneType.KK
    final CraneType MP = CraneType.MP
    final CraneType SSK = CraneType.SSK
    final CraneType BK = CraneType.BK
    final CraneType PK = CraneType.PK

    final ObjectType WORKSHOP = ObjectType.WORKSHOP
    final ObjectType WAREHOUSE = ObjectType.WAREHOUSE
    final ObjectType POWERSTATION = ObjectType.POWERSTATION
    final ObjectType RIVERPORT = ObjectType.RIVERPORT
    final ObjectType CONSTRUCTION = ObjectType.CONSTRUCTION

    private List<CraneType> _RESULT_ = null

    protected Map<String, Object> miscVariables = new HashMap<String, Object>()

    @Override
    Object getProperty(String property) {
        if (hasProperty(property))
            return super.getProperty(property)
        else
            return miscVariables.get(property)
    }

    @Override
    void setProperty(String property, Object newValue) {
        if (hasProperty(property))
            super.setProperty(property, newValue)
        else {
            if (miscVariables.containsKey(property))
                throw new CannotReassignVariableException(property)

            miscVariables.put(property, newValue)
        }
    }

    @Override
    boolean isResolved() {
        return _RESULT_ != null
    }
}
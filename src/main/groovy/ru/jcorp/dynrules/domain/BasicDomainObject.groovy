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

import ru.jcorp.dynrules.gui.controls.InputProvider
import ru.jcorp.dynrules.gui.controls.NumberInputControl
import ru.jcorp.dynrules.gui.controls.SelectInputControl
import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.model.Rule

/**
 * @author artamonov
 */
abstract class BasicDomainObject extends GroovyObjectSupport implements DomainObject {

    protected Double _W_ = null
    protected ObjectType _O_ = null
    protected Double _V_ = null
    protected Double _H_ = null

    protected boolean _Wx_ = false
    protected boolean _Ox_ = false
    protected boolean _Vx_ = false
    protected boolean _Hx_ = false

    protected String reason
    protected Set<Rule> activatedRules = new HashSet<Rule>()

    static final CraneType MK = CraneType.MK
    static final CraneType NKPK = CraneType.NKPK
    static final CraneType KK = CraneType.KK
    static final CraneType MP = CraneType.MP
    static final CraneType SSK = CraneType.SSK
    static final CraneType BK = CraneType.BK
    static final CraneType PK = CraneType.PK

    static final ObjectType WORKSHOP = ObjectType.WORKSHOP
    static final ObjectType WAREHOUSE = ObjectType.WAREHOUSE
    static final ObjectType POWERSTATION = ObjectType.POWERSTATION
    static final ObjectType RIVERPORT = ObjectType.RIVERPORT
    static final ObjectType CONSTRUCTION = ObjectType.CONSTRUCTION

    protected List<CraneType> _RESULT_ = null

    protected Map<String, Object> miscVariables = new HashMap<String, Object>()

    protected final InputProvider inputProvider

    BasicDomainObject(InputProvider inputProvider) {
        this.inputProvider = inputProvider
    }

    @Override
    boolean isResolved() {
        return _RESULT_ != null
    }

    Double getW() {
        if (!_W_ && !_Wx_) {
            _W_ = inputProvider.showInputControl(new NumberInputControl(), 'variable.input.W')
            _Wx_ = true
        }
        return _W_
    }

    ObjectType getO() {
        if (!_O_ && !_Ox_) {
            Collection<ObjectType> values = ObjectType.values()
            _O_ = inputProvider.showInputControl(
                    new SelectInputControl<ObjectType>(values), 'variable.input.O')
            _Ox_ = true
        }
        return _O_
    }

    Double getV() {
        if (!_V_ && !_Vx_) {
            _V_ = inputProvider.showInputControl(new NumberInputControl(), 'variable.input.V')
            _Vx_ = true
        }
        return _V_
    }

    Double getH() {
        if (!_H_ && !_Hx_) {
            _H_ = inputProvider.showInputControl(new NumberInputControl(), 'variable.input.H')
            _Hx_ = true
        }
        return _H_
    }

    @Override
    void setReason(String reason) {
        this.reason = reason
    }

    @Override
    String getReason() {
        return reason
    }

    @Override
    void addActivatedRule(Rule rule) {
        activatedRules.add(rule)
    }

    @Override
    Set<Rule> getActivatedRules() {
        return activatedRules
    }
}
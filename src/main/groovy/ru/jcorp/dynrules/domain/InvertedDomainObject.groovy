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

/**
 * @author ulbekov
 */
class InvertedDomainObject extends GroovyObjectSupport implements DomainObject {
    //исходные данных
    private Integer _G_ = null//грузоподъемность
    private Integer _O_ = null//объект обслуживания
    private Integer _S_ = null//скорость подъема
    private Integer _H_ = null//высота подъема

    //ответ
    private List<String> RESULT = null

    //Типы кранов
    final String MK = 'Мостовой кран'
    final String NKPK = 'Настенно-консольный поворотный кран'
    final String KK = 'Козловой кран'
    final String MP = 'Мостовой перегружатель'
    final String SSK = 'Стреловой самоходный кран'
    final String BK = 'Башенный кран'
    final String PK = 'Портальный кран'

    InvertedDomainObject() {}

    Integer getG() {
        if (!_G_) {
            //запросить грузоподъемность
        }
        return _G_
    }

    String getO() {
        if (!_O_) {
            //запроситьобъект обслуживания
        }
        return _O_
    }

    String getS() {
        if (!_S_) {
            //запроситьобъект обслуживания
        }
        return _S_
    }

    String getH() {
        if (!_H_) {
            //запроситьобъект обслуживания
        }
        return _H_
    }

    List<String> getRESULT() {
        return RESULT
    }

    void setRESULT(List<String> result) {
        RESULT = result
    }

    @Override
    Object getProperty(String property) {
        if ('G'.equals(property))
            return getG()
        else if ('O'.equals(property))
            return getO()
        else if ('S'.equals(property))
            return getS()
        else if ('H'.equals(property))
            return getH()
        else if ('RESULT'.equals(property))
            return getRESULT()

        return super.getProperty(property)
    }

    @Override
    boolean isResolved() {
        return RESULT != null
    }
}

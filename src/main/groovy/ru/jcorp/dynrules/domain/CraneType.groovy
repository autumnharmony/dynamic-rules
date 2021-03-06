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

import ru.jcorp.dynrules.DynamicRulesApp

/**
 * @author artamonov
 */
public enum CraneType {

    MK('crane.MK'),
    NKPK('crane.NKPK'),
    KK('crane.KK'),
    MP('crane.MP'),
    SSK('crane.SSK'),
    BK('crane.BK'),
    PK('crane.PK')

    final String id

    CraneType(String id) {
        this.id = id
    }

    @Override
    String toString() {
        return DynamicRulesApp.instance.getMessage(id)
    }
}
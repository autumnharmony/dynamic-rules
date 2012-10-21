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
enum ObjectType {

    WORKSHOP('object.WORKSHOP'),
    WAREHOUSE('object.WAREHOUSE'),
    POWERSTATION('object.POWERSTATION'),
    RIVERPORT('object.RIVERPORT'),
    CONSTRUCTION('object.CONSTRUCTION')

    final String id

    ObjectType(String id) {
        this.id = id
    }

    @Override
    String toString() {
        return DynamicRulesApp.instance.getMessage(id)
    }
}
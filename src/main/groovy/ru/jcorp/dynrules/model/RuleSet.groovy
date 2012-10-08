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

package ru.jcorp.dynrules.model

import ru.jcorp.dynrules.util.DslSupport

/**
 * @author artamonov
 */
class RuleSet {

    private List<Rule> rules = new ArrayList<Rule>()

    List<Rule> getRules() {
        return rules
    }

    int getSize() {
        return rules.size()
    }

    void rule(String name, Closure definition) {
        def rule = DslSupport.build(definition, new Rule(name))
        rules.add(rule)
    }

    static RuleSet build(Closure setDefinition) {
        DslSupport.build(setDefinition, new RuleSet())
    }
}
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

package ru.jcorp.dynrules

import junit.framework.TestCase
import ru.jcorp.dynrules.model.Rule
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.util.DslSupport

import static ru.jcorp.dynrules.util.DslSupport.loadClosureFromResource

/**
 * @author artamonov
 */
class ModelTest extends TestCase {

    void testRuleDsl() {
        def rule = DslSupport.build(
                {
                    _if_ = [{ var -> true }, { false }]
                    _then_ = {var -> 1}
                    _reason_ = 'Reason'
                },
                new Rule('R0'))
        assertNotNull(rule)
        assertNotNull(rule.thenStatement)
        assertEquals('Reason', rule.reason)
        assertEquals(2, rule.ifStatements.size())
    }

    void testLoadRules() {
        Closure ruleDefs = loadClosureFromResource('/rules/model-test-set.groovy')
        assertNotNull(ruleDefs)

        RuleSet ruleSet = RuleSet.build(ruleDefs)
        assertNotNull(ruleSet)
        assertEquals(2, ruleSet.size)
    }
}
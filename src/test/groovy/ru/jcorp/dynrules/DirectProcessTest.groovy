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
import ru.jcorp.dynrules.domain.TestDomainObject
import ru.jcorp.dynrules.exceptions.UnresolvedRuleSystemException
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.production.DomainObject

import ru.jcorp.dynrules.production.impl.DirectProduction

import static ru.jcorp.dynrules.util.DslSupport.loadClosureFromResource

/**
 * @author artamonov
 */
class DirectProcessTest extends TestCase {

    void testDirectProductionLogic() {
        def dObj = directProcessStream(new StringReader('3 25'), null)
        assertEquals(TestDomainObject.A, dObj.RESULT.get(0))

        dObj = directProcessStream(new StringReader('2'), null)
        assertEquals(TestDomainObject.A, dObj.RESULT.get(0))

        dObj = directProcessStream(new StringReader('3 105'), null)
        assertEquals(TestDomainObject.B, dObj.RESULT.get(0))
    }

    void testDirectProductionalLogicFail() {
        boolean exception = false
        try {
            directProcessStream(new StringReader('5'), null)
        } catch (UnresolvedRuleSystemException ignored) {
            exception = true
        }
        assertTrue(exception)
    }

    TestDomainObject directProcessStream(Reader streamReader, PrintWriter printer) {
        Closure ruleDefs = loadClosureFromResource('/rules/test-set.groovy')

        TestDomainObject dObj = new TestDomainObject(streamReader, printer)
        RuleSet ruleSet = RuleSet.build(ruleDefs)
        directLogicProcess(ruleSet, dObj)
        return dObj
    }

    private directLogicProcess(RuleSet ruleSet, DomainObject dObj) {
        new DirectProduction(dObj).perform(ruleSet)
    }

    public static void main(String[] args) {
        DirectProcessTest dtest = new DirectProcessTest()
        dtest.directProcessStream(
                new InputStreamReader(System.in),
                new PrintWriter(System.out)
        )
    }
}

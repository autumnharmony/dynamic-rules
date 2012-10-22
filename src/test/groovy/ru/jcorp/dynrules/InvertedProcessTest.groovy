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
import ru.jcorp.dynrules.domain.InvertedTestDomainObject
import ru.jcorp.dynrules.exceptions.UnresolvedRuleSystemException
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.production.impl.InvertedProduction

import static ru.jcorp.dynrules.util.DslSupport.loadClosureFromResource

/**
 * @author artamonov
 */
class InvertedProcessTest extends TestCase {

    void testInvertedProductionLogic() {
        def dObj = invertedProcessStream(new StringReader('3 25'), null)
        assertEquals(InvertedTestDomainObject.A, dObj.RESULT.get(0))
        dObj = invertedProcessStream(new StringReader('2'), null)
        assertEquals(InvertedTestDomainObject.A, dObj.RESULT.get(0))
    }

    void testInvertedProductionalLogicFail() {
        boolean exception = false
        try {
            invertedProcessStream(new StringReader('5'), null)
        } catch (UnresolvedRuleSystemException ignored) {
            exception = true
        }
        assertTrue(exception)
    }

    InvertedTestDomainObject invertedProcessStream(Reader streamReader, PrintWriter printer) {
        Closure ruleDefs = loadClosureFromResource('/rules/test-set.groovy')

        Stack<String> variablesStack = new Stack<String>()
        InvertedTestDomainObject dObj = new InvertedTestDomainObject(streamReader, printer, variablesStack)
        RuleSet ruleSet = RuleSet.build(ruleDefs)
        invertedLogicProcess(ruleSet, dObj, variablesStack)
        return dObj
    }

    private invertedLogicProcess(RuleSet ruleSet, DomainObject dObj, Stack<String> variablesStack) {
        new InvertedProduction(dObj, variablesStack).perform(ruleSet)
    }
}
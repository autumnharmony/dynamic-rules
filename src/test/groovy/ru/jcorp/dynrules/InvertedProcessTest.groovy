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
import ru.jcorp.dynrules.exceptions.CannotInputVariableException
import ru.jcorp.dynrules.exceptions.RuleStatementException
import ru.jcorp.dynrules.exceptions.UnresolvedRuleSystemException
import ru.jcorp.dynrules.model.Rule
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.production.DomainObject

import static ru.jcorp.dynrules.util.DslSupport.linkClosureToDelegate
import static ru.jcorp.dynrules.util.DslSupport.loadClosureFromResource

/**
 * @author artamonov
 */
class InvertedProcessTest extends TestCase {

    void testInvertedProductionLogic() {
        def dObj = invertedProcessStream(new StringReader('3 25'), null)
        assertEquals('AX', dObj.RESULT.get(0))
        dObj = invertedProcessStream(new StringReader('2'), null)
        assertEquals('AX', dObj.RESULT.get(0))
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
        boolean hasRules = true
        boolean resolved = false
        boolean isCompatible = true

        while (!resolved && !variablesStack.isEmpty() && hasRules && isCompatible) {
            boolean newTargetVariable = false
            List<Rule> withRule = new LinkedList<Rule>()
            hasRules = false
            boolean ruleFound = false

            for (Rule r : ruleSet.rules) {
                //add rules with current top stack variable to withRule set
                if (r.getTargetVariables().contains(variablesStack.peek())) {
                    withRule.add(r)
                    hasRules = true
                }
            }

            if (hasRules) {
                ruleFound = false
                def rule = null
                def itR = withRule.iterator()
                while (!ruleFound && itR.hasNext() && !newTargetVariable) {
                    rule = itR.next()
                    boolean conjValue = true
                    boolean allValuesResolved = true

                    def it = rule.ifStatements.iterator()
                    while (conjValue && it.hasNext()) {
                        Closure conj = linkClosureToDelegate(it.next(), dObj)

                        def conjResult
                        try {
                            conjResult = conj.call()
                        } catch (CannotInputVariableException ignored) {
                            allValuesResolved = false
                            newTargetVariable = true
                            break
                        }

                        if (conjResult instanceof Boolean)
                            conjValue = conjResult
                        else if (conjResult != null)
                            throw new RuleStatementException(rule.name)
                    }

                    if (allValuesResolved) {
                        ruleFound = conjValue
                    }
                }

                if (!newTargetVariable && ruleFound && rule != null) {
                    Closure thenClosure = linkClosureToDelegate(rule.thenStatement, dObj)
                    thenClosure.call()
                    resolved = dObj.resolved
                }
            }

            if (!newTargetVariable && !ruleFound) {
                isCompatible = false
            }
        }
        if (!isCompatible) {
            throw new UnresolvedRuleSystemException()
        }
    }
}
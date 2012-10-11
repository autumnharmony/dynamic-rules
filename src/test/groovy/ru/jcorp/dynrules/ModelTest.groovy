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
import ru.jcorp.dynrules.domain.TestDomainObject
import ru.jcorp.dynrules.exceptions.CannotInputVariableException
import ru.jcorp.dynrules.exceptions.RuleStatementException
import ru.jcorp.dynrules.exceptions.UnresolvedRuleSystemException
import ru.jcorp.dynrules.model.Rule
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.util.DslSupport

import static ru.jcorp.dynrules.util.DslSupport.linkClosureToDelegate
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

    void testDirectProductionLogic() {
        def dObj = directProcessStream(new StringReader('3 25'), null)
        assertEquals('AX', dObj.RESULT.get(0))
        dObj = directProcessStream(new StringReader('2'), null)
        assertEquals('AX', dObj.RESULT.get(0))
    }

    void testIndirectProductionLogic() {
        def dObj = indirectProcessStream(new StringReader('3 25'), null)
        assertEquals('AX', dObj.RESULT.get(0))
        dObj = indirectProcessStream(new StringReader('2'), null)
        assertEquals('AX', dObj.RESULT.get(0))
    }

    void testDirectProductionalLogicFail() {
        boolean exception = false
        try {
            directProcessStream(new StringReader('5'), null)
        } catch (UnresolvedRuleSystemException e) {
            exception = true
        }
        assertTrue(exception)
    }

    void testIndirectProductionalLogicFail() {
        boolean exception = false
        try {
            indirectProcessStream(new StringReader('5'), null)
        } catch (UnresolvedRuleSystemException e) {
            exception = true
        }
        assertTrue(exception)
    }

    TestDomainObject directProcessStream(Reader streamReader, PrintWriter printer) {
        Closure ruleDefs = loadClosureFromResource('/rules/test-set.groovy')

        Set<String> variablesQueue = new HashSet<String>()
        TestDomainObject dObj = new TestDomainObject(streamReader, printer, variablesQueue)
        RuleSet ruleSet = RuleSet.build(ruleDefs)
        directLogicProcess(ruleSet, dObj, variablesQueue)
        return dObj
    }

    InvertedTestDomainObject indirectProcessStream(Reader streamReader, PrintWriter printer) {
        Closure ruleDefs = loadClosureFromResource('/rules/test-set.groovy')

        Stack<String> variablesStack = new Stack<String>()
        InvertedTestDomainObject dObj = new InvertedTestDomainObject(streamReader, printer, variablesStack)
        RuleSet ruleSet = RuleSet.build(ruleDefs)
        indirectLogicProcess(ruleSet, dObj, variablesStack)
        return dObj
    }

    private directLogicProcess(RuleSet ruleSet, DomainObject dObj, Set<String> variablesQueue) {
        int runCount = 0
        boolean resolved = false
        int failedCount = 0

        while (!resolved &&
                !variablesQueue.isEmpty() &&
                runCount != ruleSet.size &&
                failedCount != ruleSet.size) {

            for (Rule rule : ruleSet.rules) {
                if (rule.failed)
                    continue

                boolean conjValue = true
                boolean allValuesResolved = true

                def conjIter = rule.ifStatements.iterator()
                while (conjValue && conjIter.hasNext()) {
                    Closure conj = linkClosureToDelegate(conjIter.next(), dObj)

                    def conjResult
                    try {
                        conjResult = conj.call()
                    } catch (CannotInputVariableException e) {
                        allValuesResolved = false
                        break
                    }

                    if (conjResult instanceof Boolean)
                        conjValue = conjResult
                    else if (conjResult != null)
                        throw new RuleStatementException(rule.name)
                }

                if (allValuesResolved) {
                    if (conjValue) {
                        Closure thenClosure = linkClosureToDelegate(rule.thenStatement, dObj)
                        thenClosure.call()
                        resolved = dObj.resolved
                        break
                    } else {
                        rule.failed = true
                        failedCount++
                    }
                }
            }
            runCount++
        }

        if (!resolved)
            throw new UnresolvedRuleSystemException()
    }

    private indirectLogicProcess(RuleSet ruleSet, DomainObject dObj, Stack<String> variablesStack) {
        Boolean hasRules = true
        Set<String> badValues = new HashSet<String>()

        while (!variablesStack.isEmpty() && hasRules) {
            Boolean newTargetVariable = false
            List<Rule> withRule = new LinkedList<Rule>()
            hasRules = false
            Boolean ruleFound

            for (Rule r : ruleSet.rules) {
                if (r.getTargetVariables().contains(variablesStack.peek())) {
                    withRule.add(r)
                    hasRules = true
                }
            }//add rules with current top stack variable to withRule set

            if (hasRules) {
                ruleFound = false
                def rule
                def itR = withRule.iterator()
                while (itR.hasNext() && !ruleFound) {
                    rule = itR.next()
                    Boolean conjValue = true
                    boolean allValuesResolved = true

                    if (rule.failed) {
                        conjValue = false
                    }

                    def it = rule.ifStatements.iterator()
                    while (conjValue && it.hasNext()) {
                        Closure conj = linkClosureToDelegate(it.next(), dObj)

                        def conjResult
                        try {
                            conjResult = conj.call()
                        } catch (CannotInputVariableException e) {
                            allValuesResolved = false
                            newTargetVariable = !badValues.contains(e.variable)
                            break
                        }

                        if (conjResult instanceof Boolean)
                            conjValue = conjResult
                        else if (conjResult != null)
                            throw new RuleStatementException()
                    }

                    if (allValuesResolved) {
                        ruleFound = conjValue
                    }
                }
                if (!newTargetVariable && ruleFound) {
                    Closure thenClosure = linkClosureToDelegate(rule.thenStatement, dObj)
                    thenClosure.call()
                }
            }
            if (!newTargetVariable && !ruleFound) {
                if (!variablesStack.empty() && badValues.contains(variablesStack.peek())) {
                    variablesStack.removeAll(badValues)
                }
                badValues.add(variablesStack.pop())
            }

        }
        if (!dObj.resolved) {
            throw new UnresolvedRuleSystemException()
        }
    }

    public static void main(String[] args) {
        ModelTest m = new ModelTest()
        m.directProcessStream(
                new InputStreamReader(System.in),
                new PrintWriter(System.out)
        )
    }
}
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
import ru.jcorp.dynrules.util.DslSupport
import ru.jcorp.dynrules.model.*
import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.domain.TestDomainObject
import ru.jcorp.dynrules.exceptions.CannotInputVariableException
import ru.jcorp.dynrules.exceptions.UnresolvedRuleSystemException

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
        GroovyShell sh = new GroovyShell()
        def scriptStream = getClass().getResourceAsStream('/rules/model-test-set.groovy')
        String script = scriptStream.withReader {
            reader ->
            return '{it->\n' + reader.readLines().join('\n') + '\n}'
        }
        Closure ruleDefs = sh.evaluate(script) as Closure
        assertNotNull(ruleDefs)

        RuleSet ruleSet = RuleSet.build(ruleDefs)
        assertNotNull(ruleSet)
        assertEquals(2, ruleSet.size)
    }

    void testDirectProductionLogic() {
        directProcessStream(new StringReader('3 25'), null)
        directProcessStream(new StringReader('2'), null)
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

    void directProcessStream(Reader streamReader, PrintWriter printer) {
        GroovyShell sh = new GroovyShell()
        def scriptStream = getClass().getResourceAsStream('/rules/test-set.groovy')
        String script = scriptStream.withReader {
            reader ->
            return '{it->\n' + reader.readLines().join('\n') + '\n}'
        }
        Closure ruleDefs = sh.evaluate(script) as Closure

        Queue<String> variablesQueue = new LinkedList<String>()
        DomainObject dObj = new TestDomainObject(streamReader, printer, variablesQueue)
        RuleSet ruleSet = RuleSet.build(ruleDefs)
        directLogicProcess(ruleSet, dObj, variablesQueue)
        assertEquals('AX', dObj.RESULT.get(0))
    }

    private directLogicProcess(RuleSet ruleSet, DomainObject dObj, Queue<String> variablesQueue) {
        int runCount = 0
        boolean resolved = false
        int failedCount = 0
        while (!resolved && !variablesQueue.isEmpty() && runCount != ruleSet.size && failedCount != ruleSet.size) {
            for (Rule rule : ruleSet.rules) {
                if (!rule.failed) {
                    boolean conjValue = true
                    boolean exception = false

                    def conjIter = rule.ifStatements.iterator()
                    while (conjValue && conjIter.hasNext()) {
                        Closure conj = conjIter.next()
                        conj.delegate = dObj
                        conj.resolveStrategy = groovy.lang.Closure.DELEGATE_ONLY
                        def conjResult
                        try {
                            conjResult = conj.call()
                        } catch (CannotInputVariableException e) {
                            exception = true
                            break
                        }
                        if (conjResult != null)
                            conjValue &= conjResult
                    }

                    if (!exception) {
                        if (conjValue) {
                            Closure thenClosure = rule.thenStatement
                            thenClosure.delegate = dObj
                            thenClosure.resolveStrategy = groovy.lang.Closure.DELEGATE_ONLY
                            thenClosure.call()
                            resolved = dObj.resolved
                            break
                        } else {
                            rule.setFailed(true)
                            failedCount++
                        }
                    }
                }
            }
            runCount++;
        }

        if (!resolved)
            throw new UnresolvedRuleSystemException();
    }

    public static void main(String[] args) {
        ModelTest m = new ModelTest()
        m.directProcessStream(
                new InputStreamReader(System.in),
                new PrintWriter(System.out)
        )
    }
}
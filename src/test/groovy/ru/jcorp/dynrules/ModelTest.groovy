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

    void testDirectProductionalLogic() {
        directProcessStream(new StringReader('3 25'), null)
    }

    void directProcessStream(Reader streamReader, PrintWriter printer) {
        GroovyShell sh = new GroovyShell()
        def scriptStream = getClass().getResourceAsStream('/rules/test-set.groovy')
        String script = scriptStream.withReader {
            reader ->
            return '{it->\n' + reader.readLines().join('\n') + '\n}'
        }
        Closure ruleDefs = sh.evaluate(script) as Closure

        DomainObject dObj = new TestDomainObject(streamReader, printer)
        RuleSet ruleSet = RuleSet.build(ruleDefs)
        directLogicProcess(ruleSet, dObj)
        assertEquals('AX', dObj.RESULT.get(0))
    }

    private directLogicProcess(RuleSet ruleSet, DomainObject dObj) {
        for (Rule rule : ruleSet.rules) {
            boolean conjValue = true;
            def conjIter = rule.ifStatements.iterator()
            while (conjValue && conjIter.hasNext()) {
                Closure conj = conjIter.next()
                conj.delegate = dObj
                conj.resolveStrategy = groovy.lang.Closure.DELEGATE_ONLY
                def conjResult = conj.call()
                conjValue &= conjResult
            }
            if (conjValue) {
                Closure thenClosure = rule.thenStatement
                thenClosure.delegate = dObj
                thenClosure.resolveStrategy = groovy.lang.Closure.DELEGATE_ONLY
                thenClosure.call()
                break;
            }
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
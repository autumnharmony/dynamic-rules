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

package ru.jcorp.dynrules.production.impl

import ru.jcorp.dynrules.exceptions.CannotInputVariableException
import ru.jcorp.dynrules.exceptions.RuleStatementException
import ru.jcorp.dynrules.exceptions.UnresolvedRuleSystemException
import ru.jcorp.dynrules.model.Rule
import ru.jcorp.dynrules.model.RuleSet
import ru.jcorp.dynrules.production.DomainObject
import ru.jcorp.dynrules.production.ProductionMethod

import static ru.jcorp.dynrules.util.DslSupport.linkClosureToDelegate

/**
 * @author artamonov
 */
class InvertedProduction implements ProductionMethod {

    private final DomainObject domainObject
    private final Stack<String> variablesStack

    InvertedProduction(DomainObject domainObject, Stack<String> variablesStack) {
        this.domainObject = domainObject
        this.variablesStack = variablesStack
    }

    @Override
    void perform(RuleSet ruleSet) {
        Set<Rule> badRules = new HashSet<Rule>();
        Rule previousRule = null

        while (!variablesStack.isEmpty()) {
            List<Rule> withRule = new LinkedList<Rule>()
            boolean newTargetVariable = false
            boolean ruleFound = false

            for (Rule r : ruleSet.rules) {
                //add rules with current top stack variable to withRule set
                if (r.getTargetVariables().contains(variablesStack.peek())) {
                    withRule.add(r)
                }
            }
            withRule.removeAll(badRules)

            if (!withRule.isEmpty()) {
                def rule = null

                def itR = withRule.iterator()
                while (!ruleFound && itR.hasNext() && !newTargetVariable) {
                    rule = itR.next()
                    boolean conjValue = true

                    def it = rule.ifStatements.iterator()
                    while (conjValue && it.hasNext()) {
                        Closure conj = linkClosureToDelegate(it.next(), domainObject)

                        def conjResult
                        try {
                            conjResult = conj.call()
                        } catch (CannotInputVariableException ignored) {
                            newTargetVariable = true
                            badRules.add(rule)
                            previousRule = rule
                            ruleFound = false
                            break
                        }

                        if (conjResult instanceof Boolean)
                            conjValue = conjResult
                        else if (conjResult != null)
                            throw new RuleStatementException(rule.name)
                    }

                    if (!newTargetVariable && conjValue) {
                        badRules.remove(previousRule)
                        ruleFound = true
                        previousRule = rule
                    }
                }

                if (!newTargetVariable && ruleFound && rule != null) {
                    Closure thenClosure = linkClosureToDelegate(rule.thenStatement, domainObject)
                    thenClosure.call()
                }
            }

            if (!newTargetVariable && !ruleFound) {
                variablesStack.pop();
            }
        }
        if (!domainObject.resolved)
            throw new UnresolvedRuleSystemException()
    }
}
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

import static ru.jcorp.dynrules.util.DslSupport.linkClosureToDelegate

/**
 * @author artamonov
 */
class Rule {

    private List<Closure> ifStatements = new ArrayList<Closure>()

    private Closure thenStatement = null

    private String reason = ''

    private String name = ''

    private boolean failed = false

    Rule(String name) {
        this.name = name
    }

    void set_if_(List<Closure> ifList) {
        if (ifList)
            this.ifStatements.addAll(ifList)
    }

    void set_then_(Closure then) {
        this.thenStatement = then
    }

    void set_reason_(String reason) {
        this.reason = reason
    }

    List<Closure> getIfStatements() {
        return ifStatements
    }

    Closure getThenStatement() {
        return thenStatement
    }

    String getReason() {
        return reason
    }

    String getName() {
        return name
    }

    List<String> getVariables(Closure closure) {
        DelegateStub ds = new DelegateStub()
        linkClosureToDelegate(closure, new DelegateStub())
        closure.call()
        return ds.getInspectedVariables()
    }

    List<String> getTargetVariables() {
        return getVariables(this.thenStatement)
    }

    boolean isFailed() {
        return failed
    }

    void setFailed(boolean failed) {
        this.failed = failed
    }

    protected static class DelegateStub extends GroovyObjectSupport {

        private Set<String> variables = new LinkedHashSet<String>()

        @Override
        Object getProperty(String property) {
            variables.add property
            return null
        }

        @Override
        void setProperty(String property, Object newValue) {
            variables.add property
        }

        List<String> getInspectedVariables() {
            return new ArrayList(variables)
        }
    }
}
package grails.plugin.springsecurity.graphql.authorization.instructions

import grails.plugin.springsecurity.access.vote.ClosureConfigAttribute
import org.springframework.security.access.ConfigAttribute

class ClosureInstruction implements Instruction{
    Closure closure
    /**
     * Constructor.
     * @param closure the closure
     */
    ClosureInstruction(Class clazz) {
        closure = (Closure) clazz.newInstance()
    }

    @Override
    Collection<ConfigAttribute> getAttributes() {
        return [new ClosureConfigAttribute(closure)]
    }
}

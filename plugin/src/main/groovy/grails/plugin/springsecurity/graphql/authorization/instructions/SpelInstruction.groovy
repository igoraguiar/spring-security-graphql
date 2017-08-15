package grails.plugin.springsecurity.graphql.authorization.instructions

import grails.plugin.springsecurity.ReflectionUtils
import org.springframework.security.access.ConfigAttribute

class SpelInstruction implements Instruction{

    Collection<ConfigAttribute> attributes
    SpelInstruction(String[] expressions){
        attributes = ReflectionUtils.buildConfigAttributes(expressions.toList())
    }
    @Override
    Collection<ConfigAttribute> getAttributes() {
        return attributes
    }
}

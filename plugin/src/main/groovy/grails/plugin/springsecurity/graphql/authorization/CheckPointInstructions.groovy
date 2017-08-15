package grails.plugin.springsecurity.graphql.authorization

import grails.plugin.springsecurity.graphql.authorization.instructions.Instruction
import org.springframework.security.access.ConfigAttribute

class CheckPointInstructions<T>{
    CheckPoint<T> checkPoint
    Collection<ConfigAttribute> attributes = []
    
    void addInstruction(Instruction instruction){
        attributes.addAll(instruction.getAttributes())
    }
    
}

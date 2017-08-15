package grails.plugin.springsecurity.graphql.authorization

import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.SecurityMetadataSource

class GuardInstructions implements SecurityMetadataSource{
    
    Map<CheckPoint,CheckPointInstructions> metaData = [:]
    
    @Override
    Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if(object instanceof AccessRequest){
            return metaData.get(object.checkPoint)?.attributes
        }
        throw new IllegalArgumentException("Argument is no AccessRequest")
    }

    CheckPointInstructions addInstructionsFor(CheckPoint checkPoint){
        CheckPointInstructions instructions = new CheckPointInstructions(checkPoint:checkPoint)
        metaData.put(checkPoint,instructions)
        instructions
    }    
    @Override
    Collection<ConfigAttribute> getAllConfigAttributes() {
        metaData.values().collect{it.attributes}.flatten() as Collection<ConfigAttribute>
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccessRequest)
    }
}

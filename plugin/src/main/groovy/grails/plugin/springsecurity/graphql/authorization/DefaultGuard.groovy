package grails.plugin.springsecurity.graphql.authorization

import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.context.SecurityContextHolder

import java.nio.file.AccessDeniedException

class DefaultGuard implements Guard{
    GuardInstructions instructions
    AccessDecisionManager accessDecisionManager
    
    @Override
    boolean authorize(AccessRequest accessRequest) {
        List<ConfigAttribute> howToHandle = instructions.getAttributes(accessRequest)
        
        try{
            accessDecisionManager.decide(
                SecurityContextHolder.context.authentication,
                accessRequest,
                howToHandle
            )
            return true
        }
        catch(AccessDeniedException ade){
            ade.printStackTrace()
        }
        catch(InsufficientAuthenticationException iae){
            iae.printStackTrace()
        }
    }
}

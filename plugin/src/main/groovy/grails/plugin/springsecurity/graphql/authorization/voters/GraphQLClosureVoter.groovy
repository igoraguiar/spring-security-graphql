package grails.plugin.springsecurity.graphql.authorization.voters

import grails.plugin.springsecurity.access.vote.ClosureConfigAttribute
import grails.plugin.springsecurity.graphql.authorization.AccessRequest
import grails.plugin.springsecurity.graphql.authorization.annotation.SecuredClosureDelegate
import groovy.util.logging.Slf4j
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.security.access.AccessDecisionVoter
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.core.Authentication

@Slf4j
class GraphQLClosureVoter implements AccessDecisionVoter<AccessRequest>,ApplicationContextAware{
    ApplicationContext applicationContext
    
    @Override
    boolean supports(ConfigAttribute attribute) {
        return attribute instanceof ClosureConfigAttribute
    }

    @Override
    boolean supports(Class<?> clazz) {
        clazz.isAssignableFrom AccessRequest
    }

    @Override
    int vote(Authentication authentication, AccessRequest accessRequest, Collection<ConfigAttribute> attributes) {
        ClosureConfigAttribute attribute = (ClosureConfigAttribute) attributes.find{ supports(it)}
        if(!attribute){
            return ACCESS_ABSTAIN
        }

        SecuredClosureDelegate delegate = new SecuredClosureDelegate(authentication,accessRequest,applicationContext)
        def result = attribute.closure.rehydrate(delegate,this,delegate).call()
        if(result instanceof Boolean){
            return result? ACCESS_GRANTED: ACCESS_DENIED
        }
        log.warn 'vote() returning ACCESS_DENIED because the return value from the closure call was {}, not boolean', result?.getClass()?.name
        
        ACCESS_DENIED
    }
}

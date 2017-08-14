package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.ConfigAttribute

class GraphQLSecurityManager{
    
    static final List<ConfigAttribute> NO_ATTRIBUTES = Collections.unmodifiableList(Collections.emptyList())
    
    @Autowired
    SpringSecurityService springSecurityService

    GraphQLSecurityDecisionManager decisionManager
    
    boolean isAuthenticated(Object object, List<ConfigAttribute> attributes){
        decisionManager.isAuthenticated(springSecurityService.authentication,object,attributes)
    }
}

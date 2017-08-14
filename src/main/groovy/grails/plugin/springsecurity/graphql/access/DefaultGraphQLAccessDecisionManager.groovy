package grails.plugin.springsecurity.graphql.access

import grails.plugin.springsecurity.graphql.GraphQLSecurityDecisionManager
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.core.Authentication

class DefaultGraphQLAccessDecisionManager implements GraphQLSecurityDecisionManager{

    
    @Override
    boolean isAuthenticated(Authentication authentication, Object object, List<ConfigAttribute> attributes) {
        return false
    }
}

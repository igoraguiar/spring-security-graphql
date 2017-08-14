package grails.plugin.springsecurity.graphql

import org.springframework.security.access.ConfigAttribute
import org.springframework.security.core.Authentication

interface GraphQLSecurityDecisionManager {
    boolean isAuthenticated(Authentication authentication,Object object, List<ConfigAttribute> attributes)
}

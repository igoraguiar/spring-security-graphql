package grails.plugin.springsecurity.graphql.authorization.annotation

import grails.plugin.springsecurity.graphql.authorization.AccessRequest
import grails.plugin.springsecurity.graphql.authorization.access.expression.GraphQLExpressionRoot
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.context.ApplicationContext
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.core.Authentication
import org.springframework.web.context.request.RequestContextHolder

class SecuredClosureDelegate extends GraphQLExpressionRoot{
    ApplicationContext ctx
    
    /**
     * Creates a new instance
     * @param authentication the {@link Authentication} to use. Cannot be null.
     */
    SecuredClosureDelegate(Authentication authentication, AccessRequest accessRequest, ApplicationContext ctx) {
        super(authentication,accessRequest)
        this.ctx=ctx
        setTrustResolver ctx.getBean('authenticationTrustResolver', AuthenticationTrustResolver)
        setRoleHierarchy ctx.getBean('roleHierarchy', RoleHierarchy)
        setPermissionEvaluator ctx.getBean('permissionEvaluator', PermissionEvaluator)

    }

    GrailsWebRequest getRequest(){
        RequestContextHolder.getRequestAttributes() as GrailsWebRequest
    }
    
    GrailsParameterMap getParams() {
        request?.params
    }
}

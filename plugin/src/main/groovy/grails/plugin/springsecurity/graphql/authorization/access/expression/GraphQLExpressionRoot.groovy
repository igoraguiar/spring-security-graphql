package grails.plugin.springsecurity.graphql.authorization.access.expression

import grails.plugin.springsecurity.graphql.authorization.AccessRequest
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.security.access.expression.SecurityExpressionRoot
import org.springframework.security.core.Authentication
import org.springframework.web.context.request.RequestContextHolder

class GraphQLExpressionRoot extends SecurityExpressionRoot{
    AccessRequest accessRequest
    /**
     * Creates a new instance
     * @param authentication the {@link Authentication} to use. Cannot be null.
     */
    GraphQLExpressionRoot(Authentication authentication,AccessRequest accessRequest) {
        super(authentication)
        this.accessRequest=accessRequest
    }
    
    GrailsWebRequest getRequest(){
        ((GrailsWebRequest)RequestContextHolder.getRequestAttributes())
    }
}

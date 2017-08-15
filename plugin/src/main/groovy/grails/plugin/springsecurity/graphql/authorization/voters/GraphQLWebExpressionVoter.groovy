package grails.plugin.springsecurity.graphql.authorization.voters

import grails.plugin.springsecurity.graphql.authorization.AccessRequest
import grails.plugin.springsecurity.web.access.expression.WebExpressionConfigAttribute
import groovy.util.logging.Slf4j
import org.springframework.expression.EvaluationContext
import org.springframework.security.access.AccessDecisionVoter
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.expression.ExpressionUtils
import org.springframework.security.access.expression.SecurityExpressionHandler
import org.springframework.security.core.Authentication

@Slf4j
class GraphQLWebExpressionVoter implements AccessDecisionVoter<AccessRequest>{
    SecurityExpressionHandler<AccessRequest> expressionHandler
    
    @Override
    boolean supports(ConfigAttribute attribute) {
        return attribute instanceof WebExpressionConfigAttribute
    }

    @Override
    boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccessRequest)
    }

    @Override
    int vote(Authentication authentication, AccessRequest accessRequest, Collection<ConfigAttribute> attributes) {
        assert authentication, 'authentication cannot be null'
        assert accessRequest, 'object cannot be null'
        assert attributes!=null, 'attributes cannot be null'

        log.trace 'vote() Authentication {}, AccessRequest {} ConfigAttributes {}', authentication, accessRequest, attributes

        WebExpressionConfigAttribute weca = (WebExpressionConfigAttribute) attributes.find{supports(it)}
        if (!weca) {
            log.trace 'No WebExpressionConfigAttribute found'
            return ACCESS_ABSTAIN
        }

        EvaluationContext ctx = expressionHandler.createEvaluationContext(authentication, accessRequest)

        ExpressionUtils.evaluateAsBoolean(weca.authorizeExpression, ctx) ? ACCESS_GRANTED : ACCESS_DENIED
    }
}

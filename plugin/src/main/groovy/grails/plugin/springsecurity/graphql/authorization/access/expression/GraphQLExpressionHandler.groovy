package grails.plugin.springsecurity.graphql.authorization.access.expression

import grails.plugin.springsecurity.graphql.authorization.AccessRequest
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler
import org.springframework.security.access.expression.SecurityExpressionOperations
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.core.Authentication
import org.springframework.util.Assert

class GraphQLExpressionHandler extends AbstractSecurityExpressionHandler<AccessRequest> {
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl()
    private String defaultRolePrefix = "ROLE_";

    @Override
    protected SecurityExpressionOperations createSecurityExpressionRoot(
        Authentication authentication, AccessRequest accessRequest
    ) {
        GraphQLExpressionRoot root = new GraphQLExpressionRoot(authentication,accessRequest)
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setDefaultRolePrefix(this.defaultRolePrefix);
        return root;
    }

    /**
     * Sets the {@link AuthenticationTrustResolver} to be used. The default is
     * {@link AuthenticationTrustResolverImpl}.
     *
     * @param trustResolver the {@link AuthenticationTrustResolver} to use. Cannot be
     * null.
     */
    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        Assert.notNull(trustResolver, "trustResolver cannot be null");
        this.trustResolver = trustResolver;
    }

    /**
     * <p>
     * Sets the default prefix to be added to {@link org.springframework.security.access.expression.SecurityExpressionRoot#hasAnyRole(String...)} or
     * {@link org.springframework.security.access.expression.SecurityExpressionRoot#hasRole(String)}. For example, if hasRole("ADMIN") or hasRole("ROLE_ADMIN")
     * is passed in, then the role ROLE_ADMIN will be used when the defaultRolePrefix is
     * "ROLE_" (default).
     * </p>
     *
     * <p>
     * If null or empty, then no default role prefix is used.
     * </p>
     *
     * @param defaultRolePrefix the default prefix to add to roles. Default "ROLE_".
     */
    public void setDefaultRolePrefix(String defaultRolePrefix) {
        this.defaultRolePrefix = defaultRolePrefix;
    }
}

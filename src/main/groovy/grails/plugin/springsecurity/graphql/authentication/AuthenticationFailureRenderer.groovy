package grails.plugin.springsecurity.graphql.authentication

import org.springframework.security.core.AuthenticationException

interface AuthenticationFailureRenderer<T,R> {
    R onAuthenticationFailure(T environment, AuthenticationException authentication)
}

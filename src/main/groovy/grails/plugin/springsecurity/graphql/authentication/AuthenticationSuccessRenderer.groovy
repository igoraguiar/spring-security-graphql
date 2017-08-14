package grails.plugin.springsecurity.graphql.authentication

import org.springframework.security.core.Authentication

interface AuthenticationSuccessRenderer<T,R,A extends Authentication> {
    R onAuthenticationSuccess(T environment, A authentication)
}
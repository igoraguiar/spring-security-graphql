package grails.plugin.springsecurity.graphql.credentials

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

interface CredentialsExtractor<T> {
    UsernamePasswordAuthenticationToken extractCredentials(T environment)
}
package grails.plugin.springsecurity.graphql.authentication

import grails.plugin.springsecurity.graphql.credentials.CredentialsExtractor
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import grails.views.api.http.Request
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.HttpServletRequest

@Slf4j
@CompileStatic
class GraphQLSecurityLoginHandler implements DataFetcher{
    CredentialsExtractor<DataFetchingEnvironment> credentialsExtractor
    
    AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource
    AuthenticationManager authenticationManager

    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService

    AuthenticationSuccessRenderer<DataFetchingEnvironment,Map,AccessToken> authenticationSuccessRenderer
    AuthenticationFailureRenderer<DataFetchingEnvironment,Map> authenticationFailureRenderer

    @Override
    Object get(DataFetchingEnvironment environment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        Authentication authenticationResult
        
        UsernamePasswordAuthenticationToken authenticationRequest = credentialsExtractor.extractCredentials(environment)

        Map result = Collections.emptyMap()
        boolean authenticationRequestIsCorrect = (authenticationRequest?.principal && authenticationRequest?.credentials)
        if(authenticationRequestIsCorrect){
            authenticationRequest.details = authenticationDetailsSource.buildDetails(((GrailsWebRequest)RequestContextHolder.getRequestAttributes()).request)
            
            try {

                authenticationResult = authenticationManager.authenticate(authenticationRequest)

                if (authenticationResult.authenticated) {
                    log.debug "Request authenticated. Storing the authentication result in the security context"
                    log.debug "Authentication result: ${authenticationResult}"

                    AccessToken accessToken = tokenGenerator.generateAccessToken(authenticationResult.principal as UserDetails)
                    log.debug "Generated token: ${accessToken}"

                    tokenStorageService.storeToken(accessToken.accessToken, authenticationResult.principal as UserDetails)
                    //                    authenticationEventPublisher.publishTokenCreation(accessToken)
                    //                    authenticationSuccessHandler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, accessToken)
                    SecurityContextHolder.context.setAuthentication(accessToken)

                    result = authenticationSuccessRenderer.onAuthenticationSuccess(environment, accessToken)
                }
            }
            catch(AuthenticationException e){
                result = authenticationFailureRenderer.onAuthenticationFailure(environment,e)
            }
        }
        result
    }
}

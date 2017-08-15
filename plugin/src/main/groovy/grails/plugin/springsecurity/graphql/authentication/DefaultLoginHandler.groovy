package grails.plugin.springsecurity.graphql.authentication

import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition

import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import graphql.schema.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.gorm.graphql.types.GraphQLTypeManager
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
class DefaultLoginHandler implements LoginHandler{

    AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource
    AuthenticationManager authenticationManager
    
    AccessTokenType accessTokenType
    GraphQLTypeManager typeManager
    
    String fieldName = 'login'
    String fieldDescription = ''
    String usernamePropertyName = 'username'
    String passwordPropertyName = 'password'


    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService


    protected DataFetcher<AccessToken> authorizationFetcher = new DataFetcher<AccessToken>() {
        @Override
        AccessToken get(DataFetchingEnvironment environment) {
            String username = environment.getArgument(usernamePropertyName)
            String password = environment.getArgument(passwordPropertyName)
            
            UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(
                username,password
            )
            
            AccessToken accessToken = null
            
            boolean authenticationRequestIsCorrect = (authenticationRequest?.principal && authenticationRequest?.credentials)
            if(authenticationRequestIsCorrect){
                authenticationRequest.details = authenticationDetailsSource.buildDetails(((GrailsWebRequest)RequestContextHolder.getRequestAttributes()).request)
                
                try{
                    Authentication authentication = authenticationManager.authenticate(authenticationRequest)

                    if (authentication.authenticated) {
                        log.debug "Request authenticated. Storing the authentication result in the security context"
                        log.debug "Authentication result: ${authentication}"

                        accessToken = tokenGenerator.generateAccessToken(authentication.principal as UserDetails)
                        log.debug "Generated token: ${accessToken}"

                        tokenStorageService.storeToken(accessToken.accessToken, authentication.principal as UserDetails)
                        //                    authenticationEventPublisher.publishTokenCreation(accessToken)
                        //                    authenticationSuccessHandler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, accessToken)
                        SecurityContextHolder.context.setAuthentication(accessToken)
                    }                    
                }
                catch(AuthenticationException ae){
                    log.debug("Authentication failed to {}",username,ae)
                }
            }
            return accessToken
        }
    }

    List<GraphQLArgument> buildArguments(){[
        newArgument()
            .name(usernamePropertyName)
            .type(typeManager.getType(String.class,false) as GraphQLInputType)
            .build(),
        newArgument()
            .name(passwordPropertyName)
            .type(typeManager.getType(String.class,false) as GraphQLInputType)
            .build(),
    ]}
    
    @Override
    GraphQLFieldDefinition buildFieldDefinition() {
        newFieldDefinition()
            .name(fieldName)
            .description(fieldDescription)
            .type(accessTokenType.getOutputType())
            .dataFetcher(authorizationFetcher)
            .argument(buildArguments())
            .build()
    }
}

package grails.plugin.springsecurity.graphql.authentication

import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition

import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import grails.plugin.springsecurity.rest.token.generation.jwt.AbstractJwtTokenGenerator
import grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import graphql.schema.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.gorm.graphql.types.GraphQLTypeManager
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

@Slf4j
@CompileStatic
class DefaultReloginHandler implements ReloginHandler{

    AccessTokenType accessTokenType
    GraphQLTypeManager typeManager
    
    String fieldName = 'refreshToken'
    String fieldDescription = ''
    String tokenPropertyName = 'token'
    String tokenPropertyDescription = 'Refresh token'
    

    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService


    protected DataFetcher<AccessToken> reloginFetcher = new DataFetcher<AccessToken>() {
        @Override
        AccessToken get(DataFetchingEnvironment environment) {
            String refreshToken = environment.getArgument(tokenPropertyName)
            
            try{
                UserDetails user = tokenStorageService.loadUserByToken(refreshToken);

                User principal = user ? user as User : null
                AccessToken accessToken = tokenGenerator instanceof AbstractJwtTokenGenerator?
                    tokenGenerator.generateAccessToken(principal,false):
                    tokenGenerator.generateAccessToken(principal)
                accessToken.refreshToken = refreshToken
                return accessToken
            }
            catch(Exception e){
                log.debug "Refreshing token failed for requested token {}",refreshToken
            }
        }
    }

    List<GraphQLArgument> buildArguments(){[
        newArgument()
            .name(tokenPropertyName)
            .description(tokenPropertyDescription)
            .type(typeManager.getType(String.class,false) as GraphQLInputType)
            .build(),
    ]}
    
    @Override
    GraphQLFieldDefinition buildFieldDefinition() {
        newFieldDefinition()
            .name(fieldName)
            .description(fieldDescription)
            .type(accessTokenType.getOutputType())
            .dataFetcher(reloginFetcher)
            .argument(buildArguments())
            .build()
    }
}

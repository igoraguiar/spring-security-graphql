package grails.plugin.springsecurity.graphql.authentication

import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLObjectType.newObject

import grails.plugin.springsecurity.rest.RestAuthenticationProvider
import grails.plugin.springsecurity.rest.token.AccessToken
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLObjectType
import groovy.util.logging.Slf4j
import org.grails.gorm.graphql.types.GraphQLTypeManager
import org.springframework.security.core.context.SecurityContextHolder

@Slf4j
//@CompileStatic
class DefaultAuthenticationInterceptor implements AuthenticationInterceptor{
    String fieldName
    String fieldDescription
    String objectName
    String objectDescription
    String tokenPropertyName = 'token'
    
    static final Map EMPTY = Collections.unmodifiableMap(Collections.emptyMap())


    RestAuthenticationProvider restAuthenticationProvider
    GraphQLTypeManager typeManager
    
    DataFetcher<Map> authorizationCheck = new DataFetcher<Map>() {
        @Override
        Map get(DataFetchingEnvironment environment) {
            String tokenValue = environment.getArgument(tokenPropertyName)
            AccessToken accessToken = new AccessToken(tokenValue)
            
            if(accessToken){
                accessToken = restAuthenticationProvider.authenticate(accessToken) as AccessToken

                if (accessToken.authenticated) {
                    if(accessToken.expiration == null){
                        // A refresh token is used for authentication, don't allow that
                        
                    }
                    else{
                        log.debug "Token authenticated. Storing the authentication result in the security context"
                        log.debug "Authentication result: ${accessToken}"
                        SecurityContextHolder.context.setAuthentication(accessToken)

                        return EMPTY
                        //authenticationEventPublisher.publishAuthenticationSuccess(accessToken)
                    }
                }
            }            
            return null // This ensures that other datafetchers aren't called!
        }
    }
    
    GraphQLObjectType buildDefinition(GraphQLObjectType.Builder type){
        List<GraphQLFieldDefinition> fieldDefinitions = new ArrayList<>(type.@fieldDefinitions)
        // Remove the previous instantiated fields 
        type.@fieldDefinitions.clear()

        newObject()
            .name(objectName?:fieldName.capitalize())
            .description(objectDescription)
            .fields(fieldDefinitions)
            .build()
    }
    
    
    
    
    @Override
    void intercept(GraphQLObjectType.Builder type) {
        type.field(
            newFieldDefinition()
                .name(fieldName)
                .description(fieldDescription)
                .type(buildDefinition(type))
                .dataFetcher(authorizationCheck)
                .argument(buildArgument(typeManager))
                .build()
        )
    }

    GraphQLArgument buildArgument(GraphQLTypeManager typeManager) {
        newArgument()
            .name(tokenPropertyName)
            .type(typeManager.getType(String.class,true) as GraphQLInputType)
            .build()
    }
}

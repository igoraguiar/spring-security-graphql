package grails.plugin.springsecurity.graphql.authentication

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLList.list
import static graphql.schema.GraphQLObjectType.newObject

import grails.plugin.springsecurity.rest.oauth.OauthUser
import grails.plugin.springsecurity.rest.token.AccessToken
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
import org.grails.gorm.graphql.types.GraphQLTypeManager

class DefaultAccessTokenType implements AccessTokenType{
    String objectName = 'AccessToken'
    Boolean useBearerToken
    
    String usernamePropertyName = 'username'
    String authoritiesPropertyName = 'roles'

    GraphQLTypeManager typeManager
    
    private GraphQLObjectType cachedType
    
    
    protected DataFetcher<String> usernamePropertyFetcher = new DataFetcher<String>() {
        @Override
        String get(DataFetchingEnvironment environment) {
            return ((AccessToken)environment.source).principal.username
        }
    }
    protected DataFetcher<List<String>> authoritiesPropertyFetcher = new DataFetcher<List<String>>() {
        @Override
        List<String> get(DataFetchingEnvironment environment) {
            return ((AccessToken)environment.source).authorities.collect{it.authority}
        }
    }
    protected DataFetcher<String> emailPropertyFetcher = new DataFetcher<String>() {
        @Override
        String get(DataFetchingEnvironment environment) {
            AccessToken accessToken = (AccessToken)environment.source
            if(accessToken.principal instanceof OauthUser){
                return ((OauthUser)accessToken.principal).userProfile.email
            }
            else if(accessToken.principal){
                return accessToken.principal.hasProperty('email')? accessToken.principal.email : null
            }            
        }
    }
    protected DataFetcher<String> displayNamePropertyFetcher = new DataFetcher<String>() {
        @Override
        String get(DataFetchingEnvironment environment) {
            AccessToken accessToken = (AccessToken)environment.source
            if(accessToken.principal instanceof OauthUser){
                return ((OauthUser)accessToken.principal).userProfile.displayName
            }
            else if(accessToken.principal){
                return accessToken.principal.hasProperty('displayName')? accessToken.principal.displayName : null
            }
        }
    }
    protected DataFetcher<String> accessTokenPropertyFetcher = new DataFetcher<String>() {
        @Override
        String get(DataFetchingEnvironment environment) {
            AccessToken accessToken = (AccessToken) environment.source
            if(accessToken){
                return accessToken.accessToken
            }
        }
    }
    protected DataFetcher<String> refreshTokenPropertyFetcher = new DataFetcher<String>() {
        @Override
        String get(DataFetchingEnvironment environment) {
            AccessToken accessToken = (AccessToken) environment.source
            if(accessToken){
                return accessToken.refreshToken
            }
        }
    }
    protected DataFetcher<Integer> expiresInPropertyFetcher = new DataFetcher<Integer>() {
        @Override
        Integer get(DataFetchingEnvironment environment) {
            AccessToken accessToken = (AccessToken) environment.source
            if(accessToken){
                return accessToken.expiration
            }
        }
    }
    protected DataFetcher<String> tokenTypePropertyFetcher = new DataFetcher<String>() {
        @Override
        String get(DataFetchingEnvironment environment) {
            return "Bearer"
        }
    }
    
    @Override
    GraphQLObjectType getOutputType() {
        if(cachedType==null){
            def builder = newObject()
                .name(objectName)
                .field(
                    newFieldDefinition()
                        .name(usernamePropertyName)
                        .type(typeManager.getType(String.class,false) as GraphQLOutputType)
                        .dataFetcher(usernamePropertyFetcher)
                )
                .field(
                    newFieldDefinition()
                        .name(authoritiesPropertyName)
                        .type(list(typeManager.getType(String.class,false)))
                        .dataFetcher(authoritiesPropertyFetcher)
                )
                .field(
                    newFieldDefinition()
                        .name('email')
                        .type(list(typeManager.getType(String.class,true)))
                        .dataFetcher(emailPropertyFetcher)
                )
                .field(
                    newFieldDefinition()
                        .name('displayName')
                        .type(list(typeManager.getType(String.class,true)))
                        .dataFetcher(displayNamePropertyFetcher)
                )
                
            if(useBearerToken){
                builder
                    .field(
                        newFieldDefinition()
                            .name('expires_in')
                            .type(typeManager.getType(Integer.TYPE,false))
                            .dataFetcher(expiresInPropertyFetcher)
                    )                
                    .field(
                        newFieldDefinition()
                            .name('token_type')
                            .type(typeManager.getType(String.class,false))
                            .dataFetcher(tokenTypePropertyFetcher)
                    )
                    .field(
                        newFieldDefinition()
                            .name('refresh_token')
                            .type(typeManager.getType(String.class,false))
                            .dataFetcher(refreshTokenPropertyFetcher)
                    )                
                    .field(
                        newFieldDefinition()
                            .name('access_token')
                            .type(typeManager.getType(String.class,false))
                            .dataFetcher(accessTokenPropertyFetcher)
                    )
            }

            cachedType = builder.build()            
        }
        return cachedType
    }
}

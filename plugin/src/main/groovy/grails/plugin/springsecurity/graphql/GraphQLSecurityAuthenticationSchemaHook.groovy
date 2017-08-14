package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.graphql.authentication.GraphQLSecurityLoginHandler
import grails.plugin.springsecurity.graphql.authentication.GraphQLSecurityAuthenticationHandler
import grails.plugin.springsecurity.graphql.authentication.GraphQLSecurityRefreshTokenHandler
import graphql.schema.Coercing
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.entity.GraphQLEntityNamingConvention
import org.grails.gorm.graphql.interceptor.GraphQLSchemaInterceptor

import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLList.list
import static graphql.schema.GraphQLObjectType.newObject


import org.grails.gorm.graphql.interceptor.manager.GraphQLInterceptorManager
import org.grails.gorm.graphql.plugin.GraphQLPostProcessor
import org.grails.gorm.graphql.types.GraphQLTypeManager

class GraphQLSecurityAuthenticationSchemaHook extends GraphQLPostProcessor implements GraphQLSchemaInterceptor{
    GraphQLSecurityManager securityManager
    GraphQLEntityNamingConvention entityNamingConvention

    GraphQLInterceptorManager interceptorManager
    GraphQLTypeManager typeManager
    GraphQLSecurityAuthenticationHandler authenticationHandler
    GraphQLSecurityLoginHandler loginHandler
    GraphQLSecurityRefreshTokenHandler refreshTokenHandler
    
    String queryAuthenticationFieldPropertyName
    String queryAuthenticationObjectName
    
    String mutationAuthenticationFieldPropertyName
    String mutationAuthenticationObjectName

    String tokenPropertyName
    String usernamePropertyName
    String passwordPropertyName

    String loginFieldPropertyName
    String refreshTokenFieldPropertyname
    String accessTokenObjectname    
    
    Boolean useBearerToken

    @Override
    void doWith(GraphQLTypeManager typeManager) {
        typeManager.registerType(Map,new GraphQLScalarType("Map","Java map",new Coercing<Map,Map>() {
            @Override
            Map serialize(Object input) {
                input instanceof Map? input : null
            }

            @Override
            Map parseValue(Object input) {
                return serialize(input)
            }

            @Override
            Map parseLiteral(Object input) {
                return null
            }
        }))
    }

    @Override
    void doWith(GraphQLInterceptorManager interceptorManager) {
        interceptorManager.registerInterceptor(this)
    }

    @Override
    void interceptSchema(GraphQLObjectType.Builder queryType, GraphQLObjectType.Builder mutationType) {
        buildAuthenticationField(queryAuthenticationFieldPropertyName,queryAuthenticationObjectName,queryType)
        buildAuthenticationField(mutationAuthenticationFieldPropertyName,mutationAuthenticationObjectName,mutationType)

        GraphQLObjectType accessTokenType = buildQueryAccessTokenType()
        buildLoginField(accessTokenType,queryType)
        buildRefreshTokenField(accessTokenType,queryType)
    }

    void buildAuthenticationField(String fieldName,String objectName, GraphQLObjectType.Builder type) {
        type.field(
            newFieldDefinition()
                .name(fieldName)
                .type(buildQueryAuthenticationType(objectName,type))
                .dataFetcher(authenticationHandler)
                .argument(newArgument()
                    .name(tokenPropertyName)
                    .type(typeManager.getType(String.class,true))
                    .build()
                )
        )
    }
    void buildLoginField(GraphQLObjectType accessTokenType,GraphQLObjectType.Builder type) {
        type.field(
            newFieldDefinition()
                .name(loginFieldPropertyName)
                .type(accessTokenType)
                .dataFetcher(loginHandler)
                .argument([
                    newArgument()
                        .name(usernamePropertyName)
                        .type(typeManager.getType(String.class,true))
                        .build(),
                    newArgument()
                        .name(passwordPropertyName)
                        .type(typeManager.getType(String.class,true))
                        .build(),
                ])
        )
    }
    void buildRefreshTokenField(GraphQLObjectType accessTokenType,GraphQLObjectType.Builder type) {
        type.field(
            newFieldDefinition()
                .name(refreshTokenFieldPropertyname)
                .type(accessTokenType)
                .dataFetcher(refreshTokenHandler)
                .argument(
                    newArgument()
                        .name(tokenPropertyName)
                        .type(typeManager.getType(String.class,true))
                        .build()
                )
        )
    }

    static GraphQLObjectType buildQueryAuthenticationType(String name,GraphQLObjectType.Builder type){
        List<GraphQLFieldDefinition> fieldDefinitions = new ArrayList<>(type.@fieldDefinitions)
        
        // Remove the previous instantiated fields 
        type.@fieldDefinitions.clear()
        
        newObject()
            .name(name)
            .fields(fieldDefinitions)
            .build()
    }
    
    GraphQLObjectType buildQueryAccessTokenType(){
        def builder = newObject()
            .name(accessTokenObjectname)
            .field(newFieldDefinition().name('username').type(typeManager.getType(String.class,false)))
            .field(newFieldDefinition().name('roles').type(list(typeManager.getType(String.class,false))))
            .field(newFieldDefinition().name('email').type(list(typeManager.getType(String.class,true))))
            .field(newFieldDefinition().name('displayName').type(list(typeManager.getType(String.class,true))))
        
        if(useBearerToken){
            builder
                .field(newFieldDefinition().name('expires_in').type(typeManager.getType(Integer.TYPE,false)))
                .field(newFieldDefinition().name('token_type').type(typeManager.getType(String.class,false)))
                .field(newFieldDefinition().name('refresh_token').type(typeManager.getType(String.class,false)))
                .field(newFieldDefinition().name('access_token').type(typeManager.getType(String.class,false)))
        }
        builder.build()
    }



    @Override
    void interceptEntity(PersistentEntity entity, List<GraphQLFieldDefinition.Builder> queryFields, List<GraphQLFieldDefinition.Builder> mutationFields) {
    }

}

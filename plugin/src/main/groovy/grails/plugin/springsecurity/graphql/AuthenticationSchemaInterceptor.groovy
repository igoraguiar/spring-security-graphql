package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.graphql.authentication.AuthenticationInterceptor
import grails.plugin.springsecurity.graphql.authentication.LoginHandler
import grails.plugin.springsecurity.graphql.authentication.ReloginHandler
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.interceptor.GraphQLSchemaInterceptor
import org.grails.gorm.graphql.interceptor.manager.GraphQLInterceptorManager
import org.grails.gorm.graphql.plugin.GraphQLPostProcessor

class AuthenticationSchemaInterceptor extends GraphQLPostProcessor implements GraphQLSchemaInterceptor{
    AuthenticationInterceptor queryInterceptor
    AuthenticationInterceptor mutationInterceptor

    LoginHandler loginHandler
    ReloginHandler reloginHandler
    
    @Override
    void doWith(GraphQLInterceptorManager interceptorManager) {
        interceptorManager.registerInterceptor(this)
    }

    @Override
    void interceptSchema(GraphQLObjectType.Builder queryType, GraphQLObjectType.Builder mutationType) {
        queryInterceptor.intercept(queryType)
        mutationInterceptor.intercept(mutationType)
        
        queryType.field(loginHandler.buildFieldDefinition())
        queryType.field(reloginHandler.buildFieldDefinition())
    }

    @Override
    void interceptEntity(PersistentEntity entity, List<GraphQLFieldDefinition.Builder> queryFields, List<GraphQLFieldDefinition.Builder> mutationFields) {
    }

}

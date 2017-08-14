package grails.plugin.springsecurity.graphql.access.intercept

import grails.plugin.springsecurity.graphql.GraphQLSecurityManager
import grails.plugin.springsecurity.graphql.InterceptedOperation
import graphql.schema.DataFetchingEnvironment
import org.grails.datastore.mapping.model.PersistentEntity

class GraphQLPersistentEntityInterceptor extends AbstractGraphQLSecurityInterceptor{

    GraphQLPersistentEntityInterceptor(PersistentEntity entity,GraphQLSecurityManager securityManager) {
        super(entity, securityManager)
    }

    @Override
    boolean handleIntercepted(InterceptedOperation operation, DataFetchingEnvironment environment) {
        return true
    }
}

package grails.plugin.springsecurity.graphql.access.intercept

import grails.plugin.springsecurity.graphql.GraphQLSecurityManager
import grails.plugin.springsecurity.graphql.InterceptedOperation
import grails.plugin.springsecurity.graphql.annotation.OperationType
import graphql.schema.DataFetchingEnvironment
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType
import org.grails.gorm.graphql.interceptor.GraphQLFetcherInterceptor

import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractGraphQLSecurityInterceptor implements GraphQLFetcherInterceptor{
    protected final List<InterceptedOperation> operations =
        new CopyOnWriteArrayList<InterceptedOperation>()

    GraphQLSecurityManager securityManager
    PersistentEntity entity

    AbstractGraphQLSecurityInterceptor(PersistentEntity entity, GraphQLSecurityManager securityManager){
        this.securityManager=securityManager
        this.entity=entity
    }

    private String getOperationName(DataFetchingEnvironment environment){
        environment.fields.empty? 'UNKNOWN' : environment.fields.iterator().next().name
    }
    
    
    @Override
    boolean onQuery(DataFetchingEnvironment environment, GraphQLDataFetcherType type) {
        return handleInterceptedInternal(findInterceptedOperation(getOperationName(environment)),environment)
    }

    @Override
    boolean onMutation(DataFetchingEnvironment environment, GraphQLDataFetcherType type) {
        return handleInterceptedInternal(findInterceptedOperation(getOperationName(environment)),environment)
    }

    @Override
    boolean onCustomQuery(String name, DataFetchingEnvironment environment) {
        return handleInterceptedInternal(findInterceptedOperation(name),environment)
    }

    @Override
    boolean onCustomMutation(String name, DataFetchingEnvironment environment) {
        return handleInterceptedInternal(findInterceptedOperation(name),environment)
    }

    boolean handleInterceptedInternal(InterceptedOperation operation, DataFetchingEnvironment environment){
        if(operation == null) handleNotConfigured(environment)
        handleIntercepted(operation,environment)
    }
    
    abstract boolean handleIntercepted(InterceptedOperation operation, DataFetchingEnvironment environment);
    

    void handleNotConfigured(DataFetchingEnvironment environment){

    }

    void addInterceptedOperation(InterceptedOperation interceptedOperation){
        operations.add(interceptedOperation)
    }
    
    InterceptedOperation findInterceptedOperation(String name){
        for(operation in operations){
            if(operation.operation == name){
                return operation
            }
        }
    }
}

package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.ReflectionUtils
import grails.plugin.springsecurity.annotation.Secured as PluginSecured
import grails.plugin.springsecurity.graphql.access.intercept.GraphQLPersistentEntityInterceptor
import grails.plugin.springsecurity.graphql.annotation.GraphQLOperation
import grails.plugin.springsecurity.graphql.annotation.SecuredGraphQL
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.entity.GraphQLEntityNamingConvention
import org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType
import org.grails.gorm.graphql.interceptor.GraphQLSchemaInterceptor
import org.grails.gorm.graphql.interceptor.manager.GraphQLInterceptorManager
import org.grails.gorm.graphql.plugin.GraphQLPostProcessor
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.access.annotation.Secured as SpringSecured

import java.lang.annotation.Annotation

class GraphQLSecurityAuthorizationSchemaHook extends GraphQLPostProcessor implements GraphQLSchemaInterceptor{

    GraphQLSecurityManager securityManager
    GraphQLEntityNamingConvention entityNamingConvention

    GraphQLInterceptorManager interceptorManager
    
    
    @Override
    void interceptEntity(PersistentEntity entity, List<GraphQLFieldDefinition.Builder> queryFields, List<GraphQLFieldDefinition.Builder> mutationFields) {
        GraphQLPersistentEntityInterceptor interceptor = new GraphQLPersistentEntityInterceptor(entity,securityManager)
        
        for(queryField in queryFields){
            setupOperationSecurity(interceptor,queryField)
        }
        for(mutationField in mutationFields){
            setupOperationSecurity(interceptor,mutationField)
        }
        interceptorManager.registerInterceptor(entity.javaClass,interceptor)
    }

    @Override
    void doWith(GraphQLInterceptorManager interceptorManager) {
        this.interceptorManager=interceptorManager
        interceptorManager.registerInterceptor(this)        
    }

    void setupOperationSecurity(GraphQLPersistentEntityInterceptor interceptor, GraphQLFieldDefinition.Builder operation){
        String operationName = operation.@name
        
        // Are there any secured annotations on the DataFetcher? This ensures this is a custom operation 
        // and not a system created one, they never have a Secured annotation        
        InterceptedOperation interceptedOperation = new InterceptedOperation(operation: operationName)
        
        boolean didSetupUsingAnnotation = setupUsingAnnotation(interceptedOperation,interceptor.entity)
        if(!didSetupUsingAnnotation){
            setupUsingStaticRules(interceptedOperation)
        }
        interceptor.addInterceptedOperation(interceptedOperation)
    }

    boolean setupUsingAnnotation(InterceptedOperation interceptedOperation,PersistentEntity entity){
        Class clazz = entity.javaClass
        Annotation annotation = clazz.getAnnotation(SecuredGraphQL)
        if(annotation) {
            setupUsingSecuredGraphQLAnnotation(annotation,interceptedOperation, entity)
        }
        annotation = clazz.getAnnotation(PluginSecured)
        if(annotation) {
            setupUsingSecuredPluginAnnotation(annotation,interceptedOperation)
        }
        annotation = clazz.getAnnotation(SpringSecured)
        if(annotation) {
            setupUsingSecuredSpringAnnotation(annotation,interceptedOperation)
        }        
        annotation != null
    }
    
    void setupUsingSecuredGraphQLAnnotation(SecuredGraphQL annotation,InterceptedOperation interceptedOperation,PersistentEntity entity) {
        GraphQLOperation operation = findOperation(annotation,entity,interceptedOperation.operation)
        if(operation){
            interceptedOperation.configure(operation.closure(),operation.value())
        } else{
            interceptedOperation.configure(annotation.closure(),annotation.value())
        }
    }
    static void setupUsingSecuredPluginAnnotation(PluginSecured annotation,InterceptedOperation interceptedOperation) {
        interceptedOperation.configure(annotation.closure(),annotation.value())
    }
    static void setupUsingSecuredSpringAnnotation(SpringSecured annotation,InterceptedOperation interceptedOperation) {
        interceptedOperation.configure(null,annotation.value())
    }
    void setupUsingStaticRules(InterceptedOperation interceptedOperation){
        
    }


    GraphQLOperation findOperation(SecuredGraphQL annotation,PersistentEntity entity,String operationName){
        for(operation in annotation.operations()){
            boolean matchesAnyType = operation.types().find({
                operationName == getEntityConventionName(it,entity)
            })
            if(matchesAnyType){
                return operation
            }
            boolean matchesName = operation.names().contains(operationName)
            if(matchesName){
                return operation
            }
        }
    }
    
    String getEntityConventionName(GraphQLDataFetcherType type,PersistentEntity entity){
        switch(type){
            case GraphQLDataFetcherType.CREATE:
                return entityNamingConvention.getCreate(entity)
            case GraphQLDataFetcherType.GET:
                return entityNamingConvention.getGet(entity)
            case GraphQLDataFetcherType.LIST:
                return entityNamingConvention.getList(entity)
            case GraphQLDataFetcherType.UPDATE:
                return entityNamingConvention.getUpdate(entity)
            case GraphQLDataFetcherType.DELETE:
                return entityNamingConvention.getDelete(entity)
            default: return null            
        }
    }
    
    static Collection<ConfigAttribute> createConfigAttributes(String[] expressions){
        ReflectionUtils.buildConfigAttributes(Arrays.asList(expressions))
    }
    
    @Override
    void interceptSchema(GraphQLObjectType.Builder queryType, GraphQLObjectType.Builder mutationType) {
        println "interceptSchema"
    }
}

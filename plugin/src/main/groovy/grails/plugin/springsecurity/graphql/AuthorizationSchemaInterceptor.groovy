package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.annotation.Secured as PluginSecured
import grails.plugin.springsecurity.graphql.authorization.CheckPoint
import grails.plugin.springsecurity.graphql.authorization.CheckPointInstructions
import grails.plugin.springsecurity.graphql.authorization.Guard
import grails.plugin.springsecurity.graphql.authorization.GuardInstructions
import grails.plugin.springsecurity.graphql.authorization.annotation.GqlSecured
import grails.plugin.springsecurity.graphql.authorization.annotation.Operation
import grails.plugin.springsecurity.graphql.authorization.instructions.ClosureInstruction
import grails.plugin.springsecurity.graphql.authorization.instructions.SpelInstruction
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.entity.GraphQLEntityNamingConvention
import org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType
import org.grails.gorm.graphql.interceptor.GraphQLSchemaInterceptor
import org.grails.gorm.graphql.interceptor.manager.GraphQLInterceptorManager
import org.grails.gorm.graphql.plugin.GraphQLPostProcessor
import org.grails.gorm.graphql.types.GraphQLTypeManager
import org.springframework.security.access.annotation.Secured as SpringSecured

import java.lang.annotation.Annotation

class AuthorizationSchemaInterceptor extends GraphQLPostProcessor implements GraphQLSchemaInterceptor{
    GraphQLEntityNamingConvention entityNamingConvention

    Guard guard    
    GuardInstructions instructionsManual
    
    @Override
    void interceptEntity(PersistentEntity entity, List<GraphQLFieldDefinition.Builder> queryFields, List<GraphQLFieldDefinition.Builder> mutationFields) {
        List<GraphQLFieldDefinition.Builder> allFields = queryFields + mutationFields
        
        for(GraphQLFieldDefinition.Builder field in allFields){
            
            CheckPoint checkPoint = new CheckPoint(
                field.@name,
                field.@dataFetcher,
                guard
            )
            
            // Replace the current datafetcher
            field.dataFetcher(checkPoint)
            
            CheckPointInstructions instructions = instructionsManual.addInstructionsFor(checkPoint)
            
            addStaticRulesInstructions(instructions)
            addAnnotationInstructions(entity,instructions)            
        }
    }
    
    void addStaticRulesInstructions(CheckPointInstructions instructions){
        
    }
    boolean addAnnotationInstructions(PersistentEntity entity,CheckPointInstructions instructions){
        // A datafetcher can have a secured annotation
        Annotation annotation = getSecuredAnnotation(instructions.checkPoint.subject.class)
        if(!annotation){
            Class javaClass = entity.javaClass
            
            annotation = javaClass.getAnnotation(GqlSecured)
            if(!annotation){
                annotation = getSecuredAnnotation(javaClass)
            }
        }
        if(annotation){
            addAnnotationInstructions(annotation,entity,instructions)
            true
        }
    }
    
    void addAnnotationInstructions(Annotation annotation,PersistentEntity entity,CheckPointInstructions instructions){
        if(annotation instanceof GqlSecured){
            Operation operation = findOperation(annotation,entity,instructions)
            if(operation){
                if(Operation != operation.closure()){
                    instructions.addInstruction(new ClosureInstruction(operation.closure()))
                }
                else{
                    instructions.addInstruction(new SpelInstruction(operation.value()))
                }
            }
            else{
                if(GqlSecured != annotation.closure()){
                    instructions.addInstruction(new ClosureInstruction(annotation.closure()))
                }
                else{
                    instructions.addInstruction(new SpelInstruction(annotation.value()))
                }
            }
        }
        else if(annotation instanceof PluginSecured){
            if(PluginSecured != annotation.closure()){
                instructions.addInstruction(new ClosureInstruction(annotation.closure()))
            }
            else{
                instructions.addInstruction(new SpelInstruction(annotation.value()))
            }
        }
        else{
            instructions.addInstruction(new SpelInstruction(annotation.value()))
        }
    }
    
    private static Annotation getSecuredAnnotation(Class clazz){
        Annotation secured = clazz.getAnnotation(PluginSecured)
        if(!secured){
            secured = clazz.getAnnotation(SpringSecured)
        }
        secured
    }


    @Override
    void doWith(GraphQLInterceptorManager interceptorManager) {
        interceptorManager.registerInterceptor(this)        
    }

    Operation findOperation(GqlSecured annotation, PersistentEntity entity, CheckPointInstructions instructions){
        for(operation in annotation.operations()){
            boolean matchesAnyType = operation.types().find({
                instructions.checkPoint.subjectName == getEntityConventionName(it,entity)
            })
            if(matchesAnyType){
                return operation
            }
            boolean matchesName = operation.names().contains(instructions.checkPoint.subjectName)
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
    
    @Override
    void interceptSchema(GraphQLObjectType.Builder queryType, GraphQLObjectType.Builder mutationType) {}
}

package grails.plugin.springsecurity.graphql.authorization

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class CheckPoint<T> implements DataFetcher<T>{
    String subjectName
    DataFetcher<T> subject
    Guard guard

    CheckPoint(String subjectName,DataFetcher<T> subject, Guard guard){
        this.subjectName=subjectName
        this.subject=subject
        this.guard=guard
    }
    
    @Override
    T get(DataFetchingEnvironment environment) {
        AccessRequest request = new AccessRequest(this,environment)

        if(guard.authorize(request)){
            return subject.get(environment)    
        }
    }    
    
}

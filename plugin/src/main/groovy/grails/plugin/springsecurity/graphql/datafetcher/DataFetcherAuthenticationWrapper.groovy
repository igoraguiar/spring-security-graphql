package grails.plugin.springsecurity.graphql.datafetcher

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class DataFetcherAuthenticationWrapper<T> implements DataFetcher<T>{

    DataFetcher<T> securable
    
    DataFetcherAuthenticationWrapper(DataFetcher<T> securable){
        this.securable = securable
    }
    
    @Override
    T get(DataFetchingEnvironment environment) {
//        if(matchesSecurityRule(environment)){
//            securable.get(environment)
//        }         
        return null
    }
    
    
}

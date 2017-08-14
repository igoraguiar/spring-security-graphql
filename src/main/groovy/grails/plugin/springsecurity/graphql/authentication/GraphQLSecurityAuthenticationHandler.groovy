package grails.plugin.springsecurity.graphql.authentication

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class GraphQLSecurityAuthenticationHandler implements DataFetcher{
    
    @Override
    Object get(DataFetchingEnvironment environment) {
        println "GraphQLSecurityAuthenticationHandler"
        return [:]
    }
}

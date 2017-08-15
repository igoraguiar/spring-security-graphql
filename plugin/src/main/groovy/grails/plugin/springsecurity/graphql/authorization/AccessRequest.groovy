package grails.plugin.springsecurity.graphql.authorization

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class AccessRequest {
    CheckPoint checkPoint
    DataFetchingEnvironment environment

    AccessRequest(CheckPoint checkPoint, DataFetchingEnvironment environment){
        this.checkPoint=checkPoint
        this.environment=environment
    }    
    
}

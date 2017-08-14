package grails.plugin.springsecurity.graphql.annotation

import org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType

@interface GraphQLOperation {
    String[] value() default []
    Class<?> closure() default GraphQLOperation
    
    String[] names() default []
    GraphQLDataFetcherType[] types() default []
}
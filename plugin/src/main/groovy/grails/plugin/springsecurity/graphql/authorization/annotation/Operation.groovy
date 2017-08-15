package grails.plugin.springsecurity.graphql.authorization.annotation

import org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType

@interface Operation {
    String[] value() default []
    Class<?> closure() default Operation
    
    String[] names() default []
    GraphQLDataFetcherType[] types() default []
}
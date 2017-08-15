package grails.plugin.springsecurity.graphql.authentication

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType

/**
 * Responsible for how authentication is rendered in the Schema
 */
interface AuthenticationInterceptor {
    void intercept(GraphQLObjectType.Builder type)
}

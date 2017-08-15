package grails.plugin.springsecurity.graphql.authentication

import graphql.schema.GraphQLObjectType

interface AccessTokenType {
    GraphQLObjectType getOutputType()
}

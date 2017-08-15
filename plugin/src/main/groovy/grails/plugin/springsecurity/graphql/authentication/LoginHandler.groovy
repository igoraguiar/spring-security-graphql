package grails.plugin.springsecurity.graphql.authentication

import graphql.schema.GraphQLFieldDefinition

interface LoginHandler {
    GraphQLFieldDefinition buildFieldDefinition()
}

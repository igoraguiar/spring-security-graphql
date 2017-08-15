package grails.plugin.springsecurity.graphql.authentication

import graphql.schema.GraphQLFieldDefinition

interface ReloginHandler {
    GraphQLFieldDefinition buildFieldDefinition()
}
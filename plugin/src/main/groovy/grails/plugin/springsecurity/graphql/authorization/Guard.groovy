package grails.plugin.springsecurity.graphql.authorization

interface Guard {
    boolean authorize(AccessRequest accessRequest)
} 
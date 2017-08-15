package grails.plugin.springsecurity.graphql.authorization.instructions

import org.springframework.security.access.ConfigAttribute

interface Instruction {
    Collection<ConfigAttribute> getAttributes()
}
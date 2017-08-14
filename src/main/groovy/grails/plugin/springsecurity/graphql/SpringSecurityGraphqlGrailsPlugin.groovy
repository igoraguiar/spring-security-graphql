package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.graphql.authentication.GraphQLAuthenticationSuccessRenderer
import grails.plugin.springsecurity.graphql.authentication.GraphQLSecurityAuthenticationHandler
import grails.plugin.springsecurity.graphql.authentication.GraphQLSecurityLoginHandler
import grails.plugin.springsecurity.graphql.authentication.GraphQLSecurityRefreshTokenHandler
import grails.plugin.springsecurity.graphql.credentials.GraphQLEnvironmentCredentialsExtractor
import grails.plugins.Plugin

class SpringSecurityGraphqlGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Spring Security Graphql" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/spring-security-graphql"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() { {->
        def conf = SpringSecurityUtils.securityConfig
        if (!conf || !conf.active) {
            return
        }

        SpringSecurityUtils.loadSecondaryConfig 'DefaultGraphQLSecurityConfig'
        conf = SpringSecurityUtils.securityConfig
        
        if (!conf.graphql.active) {
            return
        }
                
        // Todo: merge some props from spring-security-rest in graphQL config 
        // for example: usernamePropertyName
        
        

        boolean printStatusMessages = (conf.printStatusMessages instanceof Boolean) ? conf.printStatusMessages : true

        if (printStatusMessages) {
            println "\nConfiguring Spring Security GraphQL ${plugin.version}..."
        }

        ///*
//        SpringSecurityUtils.registerProvider 'graphQLAuthenticationProvider'

        graphQLSecurityManager(GraphQLSecurityManager)
        
        
        graphQLSecurityAuthenticationHandler(GraphQLSecurityAuthenticationHandler)
        graphQLSecurityCredentialsExtractor(GraphQLEnvironmentCredentialsExtractor)
        
        graphQLAuthenticationSuccessRenderer(GraphQLAuthenticationSuccessRenderer){
            usernamePropertyName = conf.graphql.authentication.schema.usernamePropertyName
            tokenPropertyName = conf.graphql.authentication.schema.tokenPropertyName
            authoritiesPropertyName = conf.graphql.authentication.schema.authoritiesPropertyName
            useBearerToken = conf.graphql.authentication.validation.useBearerToken
        }
        
        graphQLSecurityLoginHandler(GraphQLSecurityLoginHandler){
            credentialsExtractor = ref('graphQLSecurityCredentialsExtractor')
            authenticationDetailsSource = ref('authenticationDetailsSource')
            authenticationManager = ref('authenticationManager')
            tokenGenerator = ref('tokenGenerator')
            tokenStorageService = ref('tokenStorageService')

            authenticationSuccessRenderer = ref('graphQLAuthenticationSuccessRenderer')
        }
        graphQLSecurityRefreshTokenHandler(GraphQLSecurityRefreshTokenHandler)
        
//        graphQLSecurityReloginHandler(GraphQLSecurityAuthenticationHandler)
        
        graphQLSecurityAuthorizationSchemaHook(GraphQLSecurityAuthorizationSchemaHook){
            securityManager = ref('graphQLSecurityManager')
            entityNamingConvention = ref('graphQLEntityNamingConvention')
        }
        graphQLSecurityAuthenticationSchemaHook(GraphQLSecurityAuthenticationSchemaHook){
            securityManager = ref('graphQLSecurityManager')
            entityNamingConvention = ref('graphQLEntityNamingConvention')
            typeManager = ref('graphQLTypeManager')
            authenticationHandler = ref('graphQLSecurityAuthenticationHandler')
            loginHandler = ref('graphQLSecurityLoginHandler')
            refreshTokenHandler = ref('graphQLSecurityRefreshTokenHandler')

            queryAuthenticationFieldPropertyName = conf.graphql.authentication.schema.queryAuthenticationFieldPropertyName
            queryAuthenticationObjectName = conf.graphql.authentication.schema.queryAuthenticationObjectName

            mutationAuthenticationFieldPropertyName = conf.graphql.authentication.schema.mutationAuthenticationFieldPropertyName
            mutationAuthenticationObjectName = conf.graphql.authentication.schema.mutationAuthenticationObjectName

            tokenPropertyName = conf.graphql.authentication.schema.tokenPropertyName
            usernamePropertyName = conf.graphql.authentication.schema.usernamePropertyName
            passwordPropertyName = conf.graphql.authentication.schema.passwordPropertyName

            loginFieldPropertyName = conf.graphql.authentication.schema.loginFieldPropertyName
            refreshTokenFieldPropertyname = conf.graphql.authentication.schema.refreshTokenFieldPropertyname
            accessTokenObjectname = conf.graphql.authentication.schema.accessTokenObjectname
            useBearerToken = conf.graphql.authentication.validation.useBearerToken
        }
    }}

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}

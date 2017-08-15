package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.graphql.authentication.DefaultAccessTokenType
import grails.plugin.springsecurity.graphql.authentication.DefaultAuthenticationInterceptor
import grails.plugin.springsecurity.graphql.authentication.DefaultLoginHandler
import grails.plugin.springsecurity.graphql.authentication.DefaultReloginHandler
import grails.plugin.springsecurity.graphql.authorization.DefaultGuard
import grails.plugin.springsecurity.graphql.authorization.GuardInstructions
import grails.plugin.springsecurity.graphql.authorization.access.expression.GraphQLExpressionHandler
import grails.plugin.springsecurity.graphql.authorization.voters.GraphQLClosureVoter
import grails.plugin.springsecurity.graphql.authorization.voters.GraphQLWebExpressionVoter
import grails.plugins.Plugin

class SpringSecurityGraphqlGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.0 > *"
    
    List loadAfter = ['springSecurityCore']
    
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

    Closure doWithSpring() {
        { ->
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

            if(conf.graphql.authorization.active){
                
                //===== Setup voters 
                // Add the graphQL voters to springsecurity
                
                graphqlClosureVoter(GraphQLClosureVoter)

                graphqlExpressionHandler(GraphQLExpressionHandler) {
                    expressionParser = ref('voterExpressionParser')
                    permissionEvaluator = ref('permissionEvaluator')
                    roleHierarchy = ref('roleHierarchy')
                    trustResolver = ref('authenticationTrustResolver')
                }

                graphqlWebExpressionVoter(GraphQLWebExpressionVoter){
                    expressionHandler = ref('graphqlExpressionHandler')
                }

                SpringSecurityUtils.registerVoter'graphqlClosureVoter'
                SpringSecurityUtils.registerVoter'graphqlWebExpressionVoter'
                
                graphqlSecurityInstructions(GuardInstructions)
                
                graphqlSecurityGuard(DefaultGuard){
                    instructions = ref('graphqlSecurityInstructions')
                    accessDecisionManager = ref('accessDecisionManager')
                }
                graphQLSecurityAuthorization(AuthorizationSchemaInterceptor){
                    guard = ref('graphqlSecurityGuard')
                    instructionsManual = ref('graphqlSecurityInstructions')
                    entityNamingConvention = ref('graphQLEntityNamingConvention')
                }
            }
            
            if(conf.graphql.authentication.active){
                graphQLSecurityAccessTokenType(DefaultAccessTokenType) {
                    typeManager = ref('graphQLTypeManager')
                    useBearerToken = conf.graphql.authentication.useBearerToken
                    objectName = conf.graphql.authentication.schema.accessTokenObjectName
                    usernamePropertyName = conf.graphql.authentication.schema.accessTokenUsernamePropertyName
                    authoritiesPropertyName = conf.graphql.authentication.schema.accessTokenAuthoritiesPropertyName
                }

                graphQLSecurityLoginHandler(DefaultLoginHandler) {
                    accessTokenType = ref('graphQLSecurityAccessTokenType')
                    typeManager = ref('graphQLTypeManager')

                    authenticationDetailsSource = ref('authenticationDetailsSource')
                    authenticationManager = ref('authenticationManager')
                    tokenGenerator = ref('tokenGenerator')
                    tokenStorageService = ref('tokenStorageService')

                    fieldName = conf.graphql.authentication.schema.loginFieldName
                    fieldDescription = conf.graphql.authentication.schema.loginFieldDescription
                    usernamePropertyName = conf.graphql.authentication.schema.loginUsernamePropertyName
                    passwordPropertyName = conf.graphql.authentication.schema.loginPasswordPropertyName

                }
                graphQLSecurityReloginHandler(DefaultReloginHandler) {
                    accessTokenType = ref('graphQLSecurityAccessTokenType')
                    typeManager = ref('graphQLTypeManager')

                    tokenGenerator = ref('tokenGenerator')
                    tokenStorageService = ref('tokenStorageService')


                    fieldName = conf.graphql.authentication.schema.reloginFieldName
                    fieldDescription = conf.graphql.authentication.schema.reloginFieldDescription
                    tokenPropertyName = conf.graphql.authentication.schema.reloginTokenPropertyName
                    tokenPropertyDescription = conf.graphql.authentication.schema.reloginTokenDescription
                }

                graphQLSecurityQueryInterceptor(DefaultAuthenticationInterceptor) {
                    fieldName = conf.graphql.authentication.schema.queryFieldName
                    fieldDescription = conf.graphql.authentication.schema.queryFieldDescription
                    objectName = conf.graphql.authentication.schema.queryObjectName
                    objectDescription = conf.graphql.authentication.schema.queryObjectDescription

                    typeManager = ref('graphQLTypeManager')
                    restAuthenticationProvider = ref('restAuthenticationProvider')
                }
                graphQLSecurityMutationInterceptor(DefaultAuthenticationInterceptor) {
                    fieldName = conf.graphql.authentication.schema.mutationFieldName
                    fieldDescription = conf.graphql.authentication.schema.mutationFieldDescription
                    objectName = conf.graphql.authentication.schema.mutationObjectName
                    objectDescription = conf.graphql.authentication.schema.mutationObjectDescription

                    typeManager = ref('graphQLTypeManager')
                    restAuthenticationProvider = ref('restAuthenticationProvider')
                }
                graphQLSecurityAuthenticationSchemaHook(AuthenticationSchemaInterceptor) {
                    queryInterceptor = ref('graphQLSecurityQueryInterceptor')
                    mutationInterceptor = ref('graphQLSecurityMutationInterceptor')

                    loginHandler = ref('graphQLSecurityLoginHandler')
                    reloginHandler = ref('graphQLSecurityReloginHandler')
                }                
            }
        }
    }

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

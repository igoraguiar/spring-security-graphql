package grails.plugin.springsecurity.graphql.authentication

import graphql.schema.DataFetchingEnvironment
import org.springframework.security.core.AuthenticationException

class GraphQLAuthenticationFailureRenderer  implements AuthenticationFailureRenderer<DataFetchingEnvironment,Map>{

    @Override
    Map onAuthenticationFailure(DataFetchingEnvironment environment, AuthenticationException authentication) {
        return null
    }
}

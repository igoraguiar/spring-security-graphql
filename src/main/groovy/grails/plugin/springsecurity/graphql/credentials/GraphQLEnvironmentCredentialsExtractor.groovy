package grails.plugin.springsecurity.graphql.credentials

import graphql.schema.DataFetchingEnvironment
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@Slf4j
@CompileStatic
class GraphQLEnvironmentCredentialsExtractor implements CredentialsExtractor<DataFetchingEnvironment>{
    String usernamePropertyname = 'username'
    String passwordPropertyname = 'password'
    
    UsernamePasswordAuthenticationToken extractCredentials(DataFetchingEnvironment environment){
        String username = environment.getArgument(usernamePropertyname)
        String password = environment.getArgument(passwordPropertyname)

        log.debug "Extracted credentials from request params. Username: {}, password: {}",username,password?.size()?'[PROTECTED]':'[MISSING]'
        
        return new UsernamePasswordAuthenticationToken(username,password)
    }
}

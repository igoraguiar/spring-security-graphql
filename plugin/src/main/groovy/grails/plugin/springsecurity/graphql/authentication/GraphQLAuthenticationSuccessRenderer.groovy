package grails.plugin.springsecurity.graphql.authentication

import grails.plugin.springsecurity.rest.oauth.OauthUser
import grails.plugin.springsecurity.rest.token.AccessToken
import graphql.schema.DataFetchingEnvironment
import org.pac4j.core.profile.CommonProfile
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.Assert

class GraphQLAuthenticationSuccessRenderer implements AuthenticationSuccessRenderer<DataFetchingEnvironment,Map,AccessToken>{    
    String usernamePropertyName
    String tokenPropertyName
    String authoritiesPropertyName
    
    Boolean useBearerToken 
    
    @Override
    Map onAuthenticationSuccess(DataFetchingEnvironment environment, AccessToken accessToken) {
        Assert.isInstanceOf(UserDetails, accessToken.principal, "A UserDetails implementation is required")
        UserDetails userDetails = accessToken.principal as UserDetails

        def result = [
            (usernamePropertyName) : userDetails.username,
            (authoritiesPropertyName) : accessToken.authorities.collect { GrantedAuthority role -> role.authority }
        ]

        if (useBearerToken) {
            result.token_type = 'Bearer'
            result.access_token = accessToken.accessToken

            if (accessToken.expiration) {
                result.expires_in = accessToken.expiration
            }

            if (accessToken.refreshToken) result.refresh_token = accessToken.refreshToken

        } else {
            result["$tokenPropertyName".toString()] = accessToken.accessToken
        }
        if(userDetails instanceof OauthUser){
            CommonProfile profile = (userDetails as OauthUser).userProfile
            result.email = profile.email
            result.displayName = profile.displayName
        }
        result
    }    
}

security {

    graphql {

        active = true
        useRest = true 
        
        authentication{
            schema{
                queryAuthenticationFieldPropertyName = 'viewer'
                queryAuthenticationObjectName = 'Viewer'

                mutationAuthenticationFieldPropertyName = 'mutator'
                mutationAuthenticationObjectName = 'Mutator'

                loginFieldPropertyName = 'login'
                refreshTokenFieldPropertyname = 'refreshToken'
                accessTokenObjectname = 'AccessToken'


                tokenPropertyName = 'token'
                usernamePropertyName = 'username'
                authoritiesPropertyName = 'roles'
                passwordPropertyName = 'password'
                
            }
            validation{
                useBearerToken = true
            }
        }
        
        fieldNames{
            
        }
        
        
        useBearerTokens = true
        
    }
}
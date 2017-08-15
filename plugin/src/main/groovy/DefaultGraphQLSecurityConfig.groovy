security {
    graphql {
        active = true
        authorization{
            active = true
            
        }
        authentication{
            active = true
            useBearerToken = true
            voterNames = ['graphqlClosureVoter','graphqlWebExpressionVoter']
            schema{
                accessTokenObjectName = 'AccessToken'
                accessTokenUsernamePropertyName = 'username'
                accessTokenAuthoritiesPropertyName = 'roles'
                
                queryFieldName = 'viewer'
                queryFieldDescription = 'viewer'
                queryObjectName = 'Viewer'
                queryObjectDescription = 'Viewer'

                mutationFieldName = 'mutator'
                mutationFieldDescription = 'mutator'
                mutationObjectName = 'Mutator'
                mutationObjectDescription = 'Mutator'

                loginFieldName = 'login'
                loginFieldDescription = 'login'
                loginUsernamePropertyName = 'username'
                loginPasswordPropertyName = 'password'

                reloginFieldName = 'refreshToken'
                reloginFieldDescription = 'refreshToken'
                reloginTokenPropertyName = 'token'
                reloginTokenDescription = 'token'
            }
        }
    }
}
package grails.plugin.springsecurity.graphql.authentication

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class GraphQLSecurityRefreshTokenHandler implements DataFetcher{

    @Override
    Object get(DataFetchingEnvironment environment) {
        String token = environment.getArgument('token')
        
        return [
            username:token,
            access_token:token,
        ]
    }
}

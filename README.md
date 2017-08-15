# Spring-security-graphql

This grails-plugin allows to use spring-security inside graphQL. 

## Summary

### GraphQL 

Given a domainClass:  
```$groovy
class Foo{
    String name
}
```
GraphQL will give you a schema like: 
```$groovy
query{
    fooList
    foo(id)
}
mutation{
   fooCreate(name)
   fooUpdate(id,name)
   fooDelete(id)
}
```

### Authorization

Enabling authorization (enabled by default) allows datafetching to the graphql operations to be secured. 
@Secured is supported by default:  
```$groovy
@Secured('ROLE_ADMIN')
class Foo{
   String username 
}
```

This would make all operations; fooList, foo, fooCreate, fooUpdate and fooDelete to be checked.

> No error will be thrown if access is not granted, no results will be returned instead. 

To support authorization per operation, a custom @Secured is introduced. 
```$groovy
@Secured(value = ['ROLE_VIEWER'],operations=[
    @Operation(value = ['ROLE_ADMIN'],types = [GET,CREATE,DELETE,UPDATE]),
    @Operation(value = ['ROLE_USER'],named = ['myCustomQuery'])
])
class Foo{
   String username 
}
```

#### Todo: add static mapping 
 
## Authentication 

Enabling authentication will change the schema to: 
```$groovy
query{
    viewer(token){
        fooList
        foo(id)
    }
    login(username,password):AccessToken
    relogin(token):AccessToken
}
mutation{
    mutator(token){
       fooCreate(name)
       fooUpdate(id,name)
       fooDelete(id)    
    }
}
```

The complete API will be wrapped in a viewer / mutator field. 
Authentication is not required to be handled by graphQL and can be externalized by spring-security-core/rest for example. 


## Configuration 

See: https://raw.githubusercontent.com/tkvw/spring-security-graphql/master/plugin/src/main/groovy/DefaultGraphQLSecurityConfig.groovy


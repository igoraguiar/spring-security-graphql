package grails.plugin.springsecurity.graphql.authorization.annotation

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE ])
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@interface GqlSecured {
    String[] value() default []
    Class<?> closure() default GqlSecured
    
    Operation[] operations() default []    
}
package grails.plugin.springsecurity.graphql.authorization.annotation

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@interface Secured {
    String[] value() default []
    Class<?> closure() default Secured
    
    Operation[] operations() default []    
}
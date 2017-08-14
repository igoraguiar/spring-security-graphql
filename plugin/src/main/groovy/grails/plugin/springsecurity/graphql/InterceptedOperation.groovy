package grails.plugin.springsecurity.graphql

import grails.plugin.springsecurity.ReflectionUtils
import org.springframework.security.access.ConfigAttribute

class InterceptedOperation {
    Collection<ConfigAttribute> configAttributes = Collections.emptyList()

    String operation // if operationType is null

    Class<?> closureClass
    
    void configure(Class<?> closureClass,String[] configAttributes){
        this.closureClass = closureClass
        this.configAttributes = ReflectionUtils.buildConfigAttributes(Arrays.asList(configAttributes))
    }
}

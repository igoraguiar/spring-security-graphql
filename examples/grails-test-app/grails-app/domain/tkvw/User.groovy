package tkvw

import grails.plugin.springsecurity.graphql.authorization.annotation.Operation
import grails.plugin.springsecurity.graphql.authorization.annotation.Secured
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic
import static org.grails.gorm.graphql.fetcher.GraphQLDataFetcherType.*

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
@Secured(operations = [
    @Operation(value = ['ROLE_ADMIN'],types = [LIST,CREATE,DELETE]),
    @Operation(value = ['ROLE_USER'],types = [UPDATE])
])
class User implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
    }

    static mapping = {
	    password column: '`password`'
    }
    
    static graphql=true
}

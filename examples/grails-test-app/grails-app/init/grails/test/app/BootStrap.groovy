package grails.test.app

import tkvw.Role
import tkvw.User
import tkvw.UserRole

class BootStrap {

    def init = { servletContext ->
        User.withTransaction {
            Role admin = new Role(authority: 'ROLE_ADMIN')
            admin.save()
            User user = new User(username: 'grails',password: 'grails')
            user.save()
            
            UserRole.create(user,admin).save()
        }
    }
    def destroy = {
    }
}

grails.plugin.springsecurity.rest.token.storage.jwt.secret='qrD6h8K6S9503Q06Y6Rfk21TErImPYqa'

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'tkvw.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'tkvw.UserRole'
grails.plugin.springsecurity.authority.className = 'tkvw.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/graphql/**',     access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/static/**',      filters: 'none'],
	[pattern: '/graphql/**',     filters: 'none'],
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]


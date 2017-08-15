package tkvw

import graphql.schema.DataFetchingEnvironment
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.gorm.graphql.fetcher.impl.SingleEntityDataFetcher

class MeDataFetcher extends SingleEntityDataFetcher<User>{
    
    MeDataFetcher(PersistentEntity entity) {
        super(entity)
    }

    @Override
    User get(DataFetchingEnvironment environment) {
        return super.get(environment)
    }
}

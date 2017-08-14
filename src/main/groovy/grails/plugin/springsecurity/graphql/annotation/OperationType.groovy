package grails.plugin.springsecurity.graphql.annotation

enum OperationType {
    Create,
    Get,
    List,
    Update,
    Delete,
    Custom,
    
    OperationType(){
        
    }
    
    boolean isMutation(){
        switch(this){
            case Create:
            case Update:
            case Delete:
                return true
            default:
                return false
        } 
    }
    boolean isCustom(){
        this == Custom
    }
    boolean isQuery(){
        !custom && !mutation
    }
}
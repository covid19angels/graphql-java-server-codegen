package com.contentbig.graphql.server.codegen.model;

import com.contentbig.graphql.server.codegen.utils.Utils;
import graphql.language.*;
import lombok.NonNull;

public class DefinitionTypeDeterminer {

    public static GraphqlDefinitionType determine(@NonNull Definition definition) {
        if (definition instanceof ObjectTypeDefinition) {
            ObjectTypeDefinition typeDef = (ObjectTypeDefinition) definition;
            if (Utils.isGraphqlQueryOperation(typeDef.getName())) {
                return GraphqlDefinitionType.QUERYOPERATION;
            } else if (Utils.isGraphqlMutationOperation(typeDef.getName())) {
                return GraphqlDefinitionType.MUTATIONOPERATION;
            } else if (Utils.isGraphqlSubscriptionOperation(typeDef.getName())) {
                return GraphqlDefinitionType.SUBSCRIPTIONOPERATION;
            } else {
                return GraphqlDefinitionType.TYPE;
            }
        } else if (definition instanceof EnumTypeDefinition) {
            return GraphqlDefinitionType.ENUM;
        } else if (definition instanceof InputObjectTypeDefinition) {
            return GraphqlDefinitionType.INPUT;
        } else if (definition instanceof SchemaDefinition) {
            return GraphqlDefinitionType.SCHEMA;
        } else if (definition instanceof UnionTypeDefinition) {
            return GraphqlDefinitionType.UNION;
        } else if (definition instanceof ScalarTypeDefinition) {
            return GraphqlDefinitionType.SCALAR;
        } else if (definition instanceof InterfaceTypeDefinition) {
            return GraphqlDefinitionType.INTERFACE;
        } else if (definition instanceof DirectiveDefinition) {
            return GraphqlDefinitionType.DIRECTIVE;
        } else {
            throw new UnsupportedGraphqlDefinitionException(definition);
        }
    }

}

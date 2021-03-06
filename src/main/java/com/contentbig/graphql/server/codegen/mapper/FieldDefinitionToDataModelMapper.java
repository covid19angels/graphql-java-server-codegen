package com.contentbig.graphql.server.codegen.mapper;

import com.contentbig.graphql.server.codegen.model.MappingConfig;
import com.contentbig.graphql.server.codegen.model.OperationDefinition;
import com.contentbig.graphql.server.codegen.utils.Utils;
import graphql.language.FieldDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.contentbig.graphql.server.codegen.model.DataModelFields.*;

/**
 * Map field definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class FieldDefinitionToDataModelMapper {

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingConfig   Global mapping configuration
     * @param fieldDefinition GraphQL field definition
     * @param objectTypeName  Object type (e.g.: "Query", "Mutation" or "Subscription")
     * @return Freemarker data model of the GraphQL field
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, FieldDefinition fieldDefinition,
                                          String objectTypeName) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getApiPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, getClassName(fieldDefinition.getName(), objectTypeName));
        OperationDefinition operation = mapFieldDefinition(mappingConfig, fieldDefinition, objectTypeName);
        dataModel.put(OPERATIONS, Collections.singletonList(operation));
        return dataModel;
    }

    /**
     * Map GraphQL's FieldDefinition to a Freemarker-understandable format of operation
     *
     * @param mappingConfig  Global mapping configuration
     * @param fieldDef       GraphQL field definition
     * @param parentTypeName Name of the parent type
     * @return Freemarker-understandable format of operation
     */
    static OperationDefinition mapFieldDefinition(MappingConfig mappingConfig, FieldDefinition fieldDef, String parentTypeName) {
        OperationDefinition operation = new OperationDefinition();
        operation.setName(fieldDef.getName());
        String javaType = GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName);
        operation.setType(GraphqlTypeToJavaTypeMapper.wrapIntoSubscriptionIfRequired(mappingConfig, javaType, parentTypeName));
        operation.setAnnotations(GraphqlTypeToJavaTypeMapper.getAnnotations(mappingConfig, fieldDef.getType(), fieldDef.getName(), parentTypeName));
        operation.setParameters(InputValueDefinitionToParameterMapper.map(mappingConfig, fieldDef.getInputValueDefinitions(), fieldDef.getName()));
        operation.setDirectives(fieldDef.getDirectives());
        operation.computeConnectionFor();
        return operation;
    }

    /**
     * Examples:
     * - VersionQuery
     * - EventsByCategoryQuery
     * - CreateEventMutation
     */
    private static String getClassName(String queryDefinitionName, String objectType) {
        return Utils.capitalize(queryDefinitionName) + objectType;
    }
}

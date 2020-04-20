package com.contentbig.graphql.server.codegen.mapper;

import com.contentbig.graphql.server.codegen.model.MappingConfig;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumValueDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.contentbig.graphql.server.codegen.model.DataModelFields.*;

/**
 * Map enum definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class EnumDefinitionToDataModelMapper {

    /**
     * Map field definition to a Freemarker data model
     *
     * @param mappingConfig Global mapping configuration
     * @param enumDef       GraphQL enum definition
     * @return Freemarker data model of the GraphQL enum
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, EnumTypeDefinition enumDef) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, enumDef));
        dataModel.put(FIELDS, map(enumDef.getEnumValueDefinitions()));
        return dataModel;
    }

    /**
     * Mapper from GraphQL's EnumValueDefinition to a Freemarker-understandable format
     *
     * @param enumValueDefinitions list of GraphQL EnumValueDefinition types
     * @return list of strings
     */
    private static List<String> map(List<EnumValueDefinition> enumValueDefinitions) {
        if (enumValueDefinitions == null) {
            return Collections.emptyList();
        }
        return enumValueDefinitions.stream()
                .map(EnumValueDefinition::getName)
                .map(MapperUtils::capitalizeIfRestricted)
                .collect(Collectors.toList());
    }

}

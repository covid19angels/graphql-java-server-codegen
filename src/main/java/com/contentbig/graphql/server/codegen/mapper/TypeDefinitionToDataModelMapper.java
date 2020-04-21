package com.contentbig.graphql.server.codegen.mapper;

import com.contentbig.graphql.server.codegen.model.MappingConfig;
import com.contentbig.graphql.server.codegen.model.ParameterDefinition;
import graphql.language.Document;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ObjectTypeDefinition;

import java.util.*;
import java.util.stream.Collectors;

import static com.contentbig.graphql.server.codegen.model.DataModelFields.*;

/**
 * Map type definition to a Freemarker data model
 *
 * @author kobylynskyi
 */
public class TypeDefinitionToDataModelMapper {

    /**
     * Map type definition to a Freemarker data model
     *
     * @param mappingConfig  Global mapping configuration
     * @param typeDefinition GraphQL type definition
     * @param document       Parent GraphQL document
     * @return Freemarker data model of the GraphQL type
     */
    public static Map<String, Object> map(MappingConfig mappingConfig, ObjectTypeDefinition typeDefinition,
                                          Document document) {
        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getModelPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, MapperUtils.getClassNameWithPrefixAndSuffix(mappingConfig, typeDefinition));
        Set<String> allInterfaces = new LinkedHashSet<>(
                MapperUtils.getUnionsHavingType(mappingConfig, typeDefinition, document));
        typeDefinition.getImplements().stream()
                .map(anImplement -> GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, anImplement))
                .forEach(allInterfaces::add);
        dataModel.put(IMPLEMENTS, allInterfaces);

        // Merge attributes from the type and attributes from the interface
        Set<ParameterDefinition> allParameters = new LinkedHashSet<>(FieldDefinitionToParameterMapper
                .map(mappingConfig, typeDefinition.getFieldDefinitions(), typeDefinition.getName()));
        List<InterfaceTypeDefinition> interfaces = getInterfacesOfType(mappingConfig, typeDefinition, document);
        interfaces.stream()
                .map(i -> FieldDefinitionToParameterMapper.map(mappingConfig, i.getFieldDefinitions(), i.getName()))
                .forEach(allParameters::addAll);
        allParameters.iterator().forEachRemaining(pd -> {
            System.out.print(String.format("pd.name=[%s], type=[%s]",pd.getName(),pd.getType()));
            pd.getDirectives().iterator().forEachRemaining(s->System.out.print(",dir="+s));
            System.out.println("");
        });
        dataModel.put(FIELDS, allParameters.stream().filter(pd->!pd.getDirectives().contains("connection")).collect(Collectors.toSet()));
        dataModel.put(EQUALS_AND_HASH_CODE, mappingConfig.getGenerateEqualsAndHashCode());
        dataModel.put(TO_STRING, mappingConfig.getGenerateToString());

        return dataModel;
    }

    /**
     * Scan document and return all interfaces that given type implements.
     *
     * @param mappingConfig Global mapping configuration
     * @param definition    GraphQL type definition
     * @param document      Parent GraphQL document
     * @return all interfaces that given type implements.
     */
    private static List<InterfaceTypeDefinition> getInterfacesOfType(MappingConfig mappingConfig,
                                                                     ObjectTypeDefinition definition,
                                                                     Document document) {
        if (definition.getImplements().isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> typeImplements = definition.getImplements().stream()
                .map(type -> GraphqlTypeToJavaTypeMapper.getJavaType(mappingConfig, type))
                .collect(Collectors.toSet());
        return document.getDefinitions().stream()
                .filter(def -> def instanceof InterfaceTypeDefinition)
                .map(def -> (InterfaceTypeDefinition) def)
                .filter(def -> typeImplements.contains(def.getName()))
                .collect(Collectors.toList());
    }

}

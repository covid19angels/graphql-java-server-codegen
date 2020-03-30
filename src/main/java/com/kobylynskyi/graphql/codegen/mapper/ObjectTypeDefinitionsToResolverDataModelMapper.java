package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import com.kobylynskyi.graphql.codegen.model.OperationDefinition;
import com.kobylynskyi.graphql.codegen.model.ResolverDefinition;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kobylynskyi.graphql.codegen.model.DataModelFields.*;
import static com.kobylynskyi.graphql.codegen.model.DataModelFields.RESOLVERS;

public class ObjectTypeDefinitionsToResolverDataModelMapper {
    public static Map<String, Object> map(MappingConfig mappingConfig, Stream<ObjectTypeDefinition> objectTypeDefinitions, Stream<InterfaceTypeDefinition> interfaceTypeDefinitions) {
        List<ResolverDefinition> rds = objectTypeDefinitions.map(otd ->
                {
                    return ResolverDefinition.builder()
                            .name(otd.getName())
                            .operations(
                                    otd.getFieldDefinitions().stream()
                                            .map(fd -> FieldDefinitionToDataModelMapper.mapFieldDefinition(mappingConfig, fd, otd.getName()))
                                            .filter(od -> !od.getParameters().isEmpty())
                                            .collect(Collectors.toList()))
                            .build();
                }
        ).collect(Collectors.toList());

        List<ResolverDefinition> irds = interfaceTypeDefinitions.map(itd ->
                {
                    return ResolverDefinition.builder()
                            .name(itd.getName())
                            .operations(
                                    itd.getFieldDefinitions().stream()
                                            .map(fd -> FieldDefinitionToDataModelMapper.mapFieldDefinition(mappingConfig, fd, itd.getName()))
                                            .filter(od -> !od.getParameters().isEmpty())
                                            .collect(Collectors.toList()))
                            .build();
                }
        ).collect(Collectors.toList());
        rds.addAll(irds);

        Map<String, Object> dataModel = new HashMap<>();
        String packageName = MapperUtils.getApiPackageName(mappingConfig);
        dataModel.put(PACKAGE, packageName);
        dataModel.put(IMPORTS, MapperUtils.getImports(mappingConfig, packageName));
        dataModel.put(CLASS_NAME, "Resolvers");
        dataModel.put(RESOLVERS, rds);
        return dataModel;

    }
}

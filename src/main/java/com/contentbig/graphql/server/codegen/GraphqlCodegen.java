package com.contentbig.graphql.server.codegen;

import com.contentbig.graphql.server.codegen.mapper.*;
import com.contentbig.graphql.server.codegen.model.*;
import com.contentbig.graphql.server.codegen.supplier.MappingConfigSupplier;
import com.contentbig.graphql.server.codegen.utils.Utils;
import freemarker.template.TemplateException;
import graphql.language.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Generator of:
 * - Interface for each GraphQL query
 * - Interface for each GraphQL mutation
 * - Interface for each GraphQL subscription
 * - Class for each GraphQL data type
 * - Class for each GraphQL enum type
 * - Class for each GraphQL scalar type
 *
 * @author kobylynskyi
 * @author valinhadev
 */
@Getter
@Setter
@Slf4j
public class GraphqlCodegen {

    private List<String> schemas;
    private File outputDir;
    private MappingConfig mappingConfig;
    private MappingConfig result;

    public GraphqlCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig) {
        this(schemas, outputDir, mappingConfig, null);
    }


    public GraphqlCodegen(List<String> schemas, File outputDir, MappingConfig mappingConfig, MappingConfigSupplier externalMappingConfigSupplier) {
        this.schemas = schemas;
        this.outputDir = outputDir;
        this.mappingConfig = mappingConfig;
        this.mappingConfig.combine(externalMappingConfigSupplier != null ? externalMappingConfigSupplier.get() : null);
        initDefaultValues(mappingConfig);
    }

    private void initDefaultValues(MappingConfig mappingConfig) {
        if (mappingConfig.getModelValidationAnnotation() == null) {
            mappingConfig.setModelValidationAnnotation(DefaultMappingConfigValues.DEFAULT_VALIDATION_ANNOTATION);
        }
        if (mappingConfig.getGenerateEqualsAndHashCode() == null) {
            mappingConfig.setGenerateEqualsAndHashCode(DefaultMappingConfigValues.DEFAULT_EQUALS_AND_HASHCODE);
        }
        if (mappingConfig.getGenerateToString() == null) {
            mappingConfig.setGenerateToString(DefaultMappingConfigValues.DEFAULT_TO_STRING);
        }
    }


    public void generate() throws Exception {
        GraphqlCodegenFileCreator.prepareOutputDir(outputDir,mappingConfig);

        for (String schema : schemas) {
            long startTime = System.currentTimeMillis();
            Document document = GraphqlDocumentParser.getDocument(schema);
            addScalarsToCustomMappingConfig(document);
            processDocument(document);
            System.out.println(String.format("Finished processing schema '%s' in %d ms", schema, System.currentTimeMillis() - startTime));
        }
        genResolvers(schemas);
    }

    private void genResolvers(List<String> schemas) throws IOException, TemplateException {
        List<Definition> definitions = new ArrayList<>();
        for (String schema : schemas) {
            Document document = GraphqlDocumentParser.getDocument(schema);
            addScalarsToCustomMappingConfig(document);
            definitions.addAll(document.getDefinitions());
        }
        generateResolverInterface(definitions);
    }

    private void processDocument(Document document) throws IOException, TemplateException {
        for (Definition definition : document.getDefinitions()) {
            GraphqlDefinitionType definitionType;
            try {
                definitionType = DefinitionTypeDeterminer.determine(definition);
            } catch (UnsupportedGraphqlDefinitionException ex) {
                continue;
            }
            switch (definitionType) {
                case QUERYOPERATION:
                    generateQueryOperation((ObjectTypeDefinition) definition);
                    break;
                case MUTATIONOPERATION:
                    generateMutationOperation((ObjectTypeDefinition) definition);
                    break;
                case SUBSCRIPTIONOPERATION:
                    generateSubscriptionOperation((ObjectTypeDefinition) definition);
                    break;
                case TYPE:
                    generateType((ObjectTypeDefinition) definition, document);
                    break;
                case INTERFACE:
                    generateInterface((InterfaceTypeDefinition) definition);
                    break;
                case ENUM:
                    generateEnum((EnumTypeDefinition) definition);
                    break;
                case INPUT:
                    generateInput((InputObjectTypeDefinition) definition);
                    break;
                case UNION:
                    generateUnion((UnionTypeDefinition) definition);
            }
        }
        log.info("[KK] Generated {} definitions in folder {}", document.getDefinitions().size(),
                outputDir.getAbsolutePath());
    }

    private void generateResolverInterface(List<Definition> definitions) throws IOException, TemplateException {
       Stream<ObjectTypeDefinition> objectTypeDefinitions =  definitions.stream()
                .filter(d -> d instanceof ObjectTypeDefinition )
                .map(d->(ObjectTypeDefinition)d)
               .filter(d-> !Utils.isGraphqlQueryOperation(d.getName()) && !Utils.isGraphqlMutationOperation(d.getName()) && !Utils.isGraphqlSubscriptionOperation(d.getName()));

       Stream<InterfaceTypeDefinition> interfaceTypeDefinitions =  definitions.stream()
                .filter(d -> d instanceof InterfaceTypeDefinition )
                .map(d->(InterfaceTypeDefinition)d)
               .filter(d-> !Utils.isGraphqlQueryOperation(d.getName()) && !Utils.isGraphqlMutationOperation(d.getName()) && !Utils.isGraphqlSubscriptionOperation(d.getName()));

        Map<String, Object> dataModel = ObjectTypeDefinitionsToResolverDataModelMapper.map(mappingConfig, objectTypeDefinitions,interfaceTypeDefinitions);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.resolversTemplate, dataModel, outputDir);

    }

    private void generateUnion(UnionTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = UnionDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.unionTemplate, dataModel, outputDir);
    }

    private void generateInterface(InterfaceTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = InterfaceDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.interfaceTemplate, dataModel, outputDir);
    }

    private void generateQueryOperation(ObjectTypeDefinition definition) throws IOException, TemplateException {
//            for (FieldDefinition fieldDef : definition.getFieldDefinitions()) {
//                Map<String, Object> dataModel = FieldDefinitionToDataModelMapper.map(mappingConfig, fieldDef, definition.getName());
//                GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
//            }
            // We need to generate a root object to workaround https://github.com/facebook/relay/issues/112
            Map<String, Object> dataModel = ObjectDefinitionToDataModelMapper.map(mappingConfig, definition);
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.queryOperationsTemplate, dataModel, outputDir);
    }

    private void generateMutationOperation(ObjectTypeDefinition definition) throws IOException, TemplateException {
//            for (FieldDefinition fieldDef : definition.getFieldDefinitions()) {
//                Map<String, Object> dataModel = FieldDefinitionToDataModelMapper.map(mappingConfig, fieldDef, definition.getName());
//                GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
//            }
            // We need to generate a root object to workaround https://github.com/facebook/relay/issues/112
            Map<String, Object> dataModel = ObjectDefinitionToDataModelMapper.map(mappingConfig, definition);
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.mutationOperationsTemplate, dataModel, outputDir);
    }

    private void generateSubscriptionOperation(ObjectTypeDefinition definition) throws IOException, TemplateException {
//            for (FieldDefinition fieldDef : definition.getFieldDefinitions()) {
//                Map<String, Object> dataModel = FieldDefinitionToDataModelMapper.map(mappingConfig, fieldDef, definition.getName());
//                GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.operationsTemplate, dataModel, outputDir);
//            }
            // We need to generate a root object to workaround https://github.com/facebook/relay/issues/112
            Map<String, Object> dataModel = ObjectDefinitionToDataModelMapper.map(mappingConfig, definition);
            GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.subscriptionOperationsTemplate, dataModel, outputDir);
    }

    private void generateType(ObjectTypeDefinition definition, Document document) throws IOException, TemplateException {
        Map<String, Object> dataModel = TypeDefinitionToDataModelMapper.map(mappingConfig, definition, document);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private void generateInput(InputObjectTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = InputDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.typeTemplate, dataModel, outputDir);
    }

    private void generateEnum(EnumTypeDefinition definition) throws IOException, TemplateException {
        Map<String, Object> dataModel = EnumDefinitionToDataModelMapper.map(mappingConfig, definition);
        GraphqlCodegenFileCreator.generateFile(FreeMarkerTemplatesRegistry.enumTemplate, dataModel, outputDir);
    }

    private void addScalarsToCustomMappingConfig(Document document) {
        for (Definition definition : document.getDefinitions()) {
            if (definition instanceof ScalarTypeDefinition) {
                String scalarName = ((ScalarTypeDefinition) definition).getName();
                mappingConfig.putCustomTypeMappingIfAbsent(scalarName, "String");
            }
        }
    }

}

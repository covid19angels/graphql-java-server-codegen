package com.contentbig.graphql.server.codegen;

import com.contentbig.graphql.server.codegen.model.DataModelFields;
import com.contentbig.graphql.server.codegen.model.MappingConfig;
import com.contentbig.graphql.server.codegen.utils.Utils;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;

/**
 * Utility class for generating files
 *
 * @author kobylynskyi
 */
class GraphqlCodegenFileCreator {

    private static final String EXTENSION = ".java";

    static void generateFile(Template template, Map<String, Object> dataModel, File outputDir)
            throws IOException, TemplateException {
        String fileName = dataModel.get(DataModelFields.CLASS_NAME) + EXTENSION;
        File fileOutputDir = getFileTargetDirectory(dataModel, outputDir);
        File javaSourceFile = new File(fileOutputDir, fileName);
        boolean fileCreated = javaSourceFile.createNewFile();
        if (!fileCreated) {
            throw new FileAlreadyExistsException("File already exists: " + javaSourceFile.getPath());
        }
        System.out.println(String.format("javaSourceFile=%s",javaSourceFile.getAbsolutePath()));
        template.process(dataModel, new FileWriter(javaSourceFile));
    }

    /**
     * Prepare outputDir and api/model/resolver folder under outputDir
     *
     * Don't delete outputDir as it may be shared in multiple GraphqlCodegenTask
     *
     * @param outputDir
     * @param mappingConfig
     * @throws IOException
     */
    static void prepareOutputDir(File outputDir, MappingConfig mappingConfig) throws IOException {
        // Don't delete outputDir as it will be shared with multiple GraphqlCodegenTask
        // Utils.deleteDir(outputDir);
        Utils.createDirIfAbsent(outputDir);

        // api package folder
        String packageName = mappingConfig.getApiPackageName();
        File  targetDir = new File(outputDir, packageName.toString().replace(".", File.separator));
        Utils.deleteDir(targetDir);

        // model package folder
        packageName = mappingConfig.getModelPackageName();
        targetDir = new File(outputDir, packageName.toString().replace(".", File.separator));
        Utils.deleteDir(targetDir);

    }

    private static File getFileTargetDirectory(Map<String, Object> dataModel, File outputDir) throws IOException {
        File targetDir;
        Object packageName = dataModel.get(DataModelFields.PACKAGE);
        if (packageName != null && !Utils.isBlank(packageName.toString())) {
            targetDir = new File(outputDir, packageName.toString().replace(".", File.separator));
        } else {
            targetDir = outputDir;
        }
        Utils.createDirIfAbsent(targetDir);
        return targetDir;
    }

}

package com.contentbig.graphql.server.codegen;

import com.contentbig.graphql.server.codegen.model.SchemaValidationException;
import graphql.GraphQLException;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;

/**
 * Validator of GraphQL schemas
 *
 * @author kobylynskyi
 */
@AllArgsConstructor
public class GraphqlCodegenValidate {

    private final List<String> schemas;

    public void validate() throws IOException {
        for (String schema : schemas) {
            try {
                long startTime = System.currentTimeMillis();
                GraphqlDocumentParser.getDocument(schema);
                System.out.println(String.format("Validated schema '%s' in %d ms",
                        schema, System.currentTimeMillis() - startTime));
            } catch (GraphQLException e) {
                throw new SchemaValidationException(e.getMessage());
            }
        }
    }

}

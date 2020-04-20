package com.contentbig.graphql.server.codegen.model;

/**
 * Exception that indicates invalid GraphQL schema
 *
 * @author kobylynskyi
 */
public class SchemaValidationException extends RuntimeException {

    public SchemaValidationException(String message) {
        super("GraphQL schema is invalid: " + message);
    }

}

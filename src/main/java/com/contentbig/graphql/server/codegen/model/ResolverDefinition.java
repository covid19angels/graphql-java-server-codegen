package com.contentbig.graphql.server.codegen.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Freemarker-understandable format of operation (Query/Mutation/Subscription)
 *
 * @author kobylynskyi
 */
@Data
@Builder
public class ResolverDefinition {

    private String name;
    private List<OperationDefinition> operations ;

}

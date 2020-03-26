package com.kobylynskyi.graphql.codegen.model;

import com.google.gson.Gson;
import graphql.language.Directive;
import graphql.language.StringValue;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
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

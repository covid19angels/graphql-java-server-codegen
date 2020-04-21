package com.contentbig.graphql.server.codegen.model;

import com.google.gson.Gson;
import graphql.language.Directive;
import graphql.language.StringValue;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Freemarker-understandable format of operation (Query/Mutation/Subscription)
 *
 * @author kobylynskyi
 */
@Data
public class OperationDefinition {

    private String name;
    private String type;
    private List<String> annotations = new ArrayList<>();
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private List<Directive> directives = new ArrayList<>();

    private String connectionFor;

    public void computeConnectionFor() {
        directives.stream()
                .filter(s -> s.getName().equalsIgnoreCase("connection"))
                .map(d -> (StringValue) d.getArgument("for").getValue())
                .findFirst()
                .ifPresent(stringValue -> {
                    connectionFor = stringValue.getValue();
                });

        System.out.println(String.format("OperationDefinition.name=[%s], type=[%s] connectionFor=%s",name,type,connectionFor));
    }
}

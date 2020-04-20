package com.contentbig.graphql.server.codegen.model;

import com.google.gson.Gson;
import graphql.language.Directive;
import graphql.language.StringValue;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    Gson gson = new Gson();

    public Boolean noRelayConnectionDirective() {
        return directives.stream()
                .filter(s -> s.getName().equalsIgnoreCase("connection"))
                .findFirst()
                .isEmpty();
    }
    public String connectionFor(){
        return directives.stream()
                .filter(s->s.getName().equalsIgnoreCase("connection"))
                .map(d->(StringValue)d.getArgument("for").getValue())
                .findFirst()
                .map(sv->sv.getValue())
                .get();
    }
}

package com.contentbig.graphql.server.codegen.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of method parameter and field definition
 *
 * @author kobylynskyi
 */
@Data
public class ParameterDefinition {

    private String type;
    private String name;
    private List<String> annotations = new ArrayList<>();
    private List<String> directives = new ArrayList<>();

    public Boolean noRelayConnectionDirective() {

     if(directives.stream()
             .filter(s -> s.equalsIgnoreCase("connection"))
             .findFirst()==null)
     {
         return true;
     }

     return false;

    }

}

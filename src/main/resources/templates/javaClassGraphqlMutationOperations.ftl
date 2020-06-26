// generated with template javaClassGraphqlMutationOperations.ftl

<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>
import graphql.schema.DataFetchingEnvironment;
import graphql.relay.Connection;

public interface ${className} {
// KKMutation
<#list operations as operation>
<#if operation.connectionFor?has_content>
    public Connection<${operation.connectionFor}GQO> ${operation.name}(<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>, DataFetchingEnvironment env);
<#else>
    <#list operation.annotations as annotation>
    @${annotation}
    </#list>
    ${operation.type} ${operation.name}(<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>, DataFetchingEnvironment env) throws Exception;
</#if>
</#list>
}
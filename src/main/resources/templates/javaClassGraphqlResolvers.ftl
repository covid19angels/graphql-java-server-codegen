// generated with template javaClassGraphqlResolvers.ftl
<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>
import graphql.schema.DataFetchingEnvironment;
import graphql.relay.Connection;

public class Resolvers{
<#list resolvers as resolver>
    public interface ${resolver.name} {
    // KK
    <#list resolver.operations as operation>
    <#if operation.connectionFor?has_content>
        public Connection<${operation.connectionFor}GQO> ${operation.name}(${resolver.name}GQO parent,<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>, DataFetchingEnvironment env);
    <#else>
        <#list operation.annotations as annotation>
        @${annotation}
        </#list>
        ${operation.type} ${operation.name}(${resolver.name}GQO parent,<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>, DataFetchingEnvironment env) throws Exception;
    </#if>
    </#list>
    }
</#list>
}
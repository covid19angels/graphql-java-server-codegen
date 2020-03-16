<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>
import graphql.schema.DataFetchingEnvironment;
import graphql.relay.Connection;

public interface ${className} {
// KK
<#list operations as operation><#if operation.noRelayConnectionDirective()>
<#list operation.annotations as annotation>
    @${annotation}
</#list>
    ${operation.type} ${operation.name}(<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>, DataFetchingEnvironment env) throws Exception;
<#else>
    public Connection<${operation.connectionFor()}GQO> ${operation.name}(<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>, DataFetchingEnvironment env);
</#if></#list>
}
// generated with template javaClassGraphqlSubscriptionOperations.ftl

<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>
import graphql.schema.DataFetchingEnvironment;
import org.reactivestreams.Publisher;

public interface ${className} {
// KKSubscription
<#list operations as operation>
    <#list operation.annotations as annotation>
    @${annotation}
    </#list>
    Publisher<${operation.type}> ${operation.name}(<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>, DataFetchingEnvironment env) throws Exception;
</#list>
}
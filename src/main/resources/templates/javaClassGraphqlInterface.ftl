// generated with template javaClassGraphqlInterface.ftl
<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>
<#if className == "NodeGQO" >
public interface ${className} extends com.shopify.graphql.support.Node {
    String getGraphQlTypeName();
<#else>
public interface ${className} <#if implements?has_content>implements <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>{
</#if>
<#list fields as field>
    <#if field.connectionFor?has_content>
    <#else>
    ${field.type} get${field.name?cap_first}();
    </#if>
</#list>
}
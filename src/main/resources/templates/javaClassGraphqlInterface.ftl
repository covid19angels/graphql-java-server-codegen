// generated with template javaClassGraphqlInterface.ftl
<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

public interface ${className} <#if implements?has_content>implements <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>{

<#list fields as field>
    <#if field.connectionFor?has_content>
    <#else>
    ${field.type} get${field.name?cap_first}();
    </#if>
</#list>
}
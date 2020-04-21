// generated with template javaClassGraphqlType.ftl

<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ${className} <#if implements?has_content>implements <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>{

<#list fields as field>
    <#list field.annotations as annotation>
    @${annotation}
    </#list>
    private ${field.type} ${field.name};
</#list>

}
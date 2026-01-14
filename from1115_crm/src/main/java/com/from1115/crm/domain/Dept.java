// 文件路径：src/main/java/com/from1115/crm/domain/Dept.java

package com.from1115.crm.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 部门实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dept {
    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称，例如：研发部、销售部
     */
    private String name;
}
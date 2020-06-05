package com;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单
 */
@Data
public class Menu implements Serializable {

    private Integer id;

    private String parentCode;

    private String code;

    private String name;

    private Integer parentId;

    private Integer level;

    private List<Menu> children;

    public Menu() {
    }

    public Menu(Integer id, String code, String name, String parentCode, Integer parentId, Integer level) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
        this.parentId = parentId;
        this.level = level;
    }
}
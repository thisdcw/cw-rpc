package com.cw.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author thisdcw-com
 * @date 2024/8/20 14:37
 */
@Data
public class User implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package org.destiny.activiti;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

/**
 * @author wangkang
 * @version 1.8.0_191
 * create by 2018-12-19 18:30
 * --------------------------------------------------------------
 * <p>
 * --------------------------------------------------------------
 * Copyright: Copyright (c) 2018
 */
public class ShareniuLoopVariableUtils {

    public static void setLoopVariable(ExecutionEntity execution, String variableName, Object value) {
        ExecutionEntity parent = execution.getParent();
        parent.setVariableLocal(variableName, value);
    }

    public static Integer getLoopVariable(ExecutionEntity execution, String variableName) {
        Object value = execution.getVariable(variableName);
        ExecutionEntity parent = execution.getParent();
        while (value == null && parent != null) {
            value = parent.getVariableLocal(variableName);
            parent = parent.getParent();
        }
        return (Integer) (value != null ? value : 0);
    }
}

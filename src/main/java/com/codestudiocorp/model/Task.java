package com.codestudiocorp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Task {
    private String taskName;
    private int priorityOrder;
    private List<Integer> activityTypes;
    private boolean required;
    private boolean completed;
}

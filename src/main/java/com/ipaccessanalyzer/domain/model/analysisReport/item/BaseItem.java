package com.ipaccessanalyzer.domain.model.analysisReport.item;

import com.ipaccessanalyzer.domain.enums.FieldSpec;

import lombok.Getter;

@Getter
public abstract class BaseItem {
    protected final FieldSpec fieldSpec;

    protected BaseItem(FieldSpec fieldSpec) {
        this.fieldSpec = fieldSpec;
    }

    public String getFieldName() {
        return fieldSpec.getDescription();
    }
}

package com.ipaccessanalyzer.domain.model.analysisReport.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ipaccessanalyzer.domain.enums.FieldSpec;

public class SamplingReportItem extends BaseItem {
    private final List<String> samples = new ArrayList<>();
    private static final int MAX_SAMPLES = 5;

    public SamplingReportItem(FieldSpec fieldSpec) {
        super(fieldSpec);
    }

    public void record(String value) {
        if (samples.size() < MAX_SAMPLES) {
            samples.add(value);
        }
    }

    public List<String> getSamples() {
        return Collections.unmodifiableList(samples);
    }
}
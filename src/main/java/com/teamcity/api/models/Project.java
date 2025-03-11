package com.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamcity.api.annotations.Parameterizable;
import com.teamcity.api.annotations.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends BaseModel {
    @Random
    @Parameterizable
    private String id;
    @Random
    private String name;
    private String locator;
    private Locator parentProject = new Locator("_Root");
    @Builder.Default
    private boolean copyAllAssociatedSettings = true;
}

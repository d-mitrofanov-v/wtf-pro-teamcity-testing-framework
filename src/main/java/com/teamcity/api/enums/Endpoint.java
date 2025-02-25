package com.teamcity.api.enums;

import com.teamcity.api.models.BaseModel;
import com.teamcity.api.models.BuildType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILT_TYPES("/app/rest/BuildType", BuildType.class);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
}

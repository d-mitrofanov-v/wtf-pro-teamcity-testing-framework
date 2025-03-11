package com.teamcity.api.spec;

import com.teamcity.api.config.Config;
import com.teamcity.api.models.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class Specs {
    private static Specs spec;

    private static RequestSpecBuilder reqBuilder() {
        RequestSpecBuilder requestBuilder = new RequestSpecBuilder();
        requestBuilder
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter());
        return requestBuilder;
    }

    public static RequestSpecification unauthSpec() {
        return reqBuilder().build();
    }

    public static RequestSpecification superUserAuth() {
        RequestSpecBuilder requestBuilder = reqBuilder();
        return requestBuilder
                .setBaseUri("http://%s:%s@%s".formatted("", Config.getProperty("superUserToken"), Config.getProperty("host")))
                .build();
    }

    public static RequestSpecification authSpec(User user) {
        RequestSpecBuilder requestBuilder = reqBuilder();
        return requestBuilder
                .setBaseUri("http://%s:%s@%s".formatted(user.getUsername(), user.getPassword(), Config.getProperty("host")))
                .build();
    }
}


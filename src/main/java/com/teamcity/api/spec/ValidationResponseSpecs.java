package com.teamcity.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ValidationResponseSpecs {

    private static ResponseSpecification buildResponseSpec(int statusCode, String expectedMessage, Object... messageParams) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectBody(Matchers.containsString(String.format(expectedMessage, messageParams)))
                .build();
    }

    public static ResponseSpecification checkProjectWithNameAlreadyExist(String projectName) {
        return buildResponseSpec(HttpStatus.SC_BAD_REQUEST, "Project with this name already exists: %s", projectName);
    }

    public static ResponseSpecification checkProjectWithIdAlreadyExist(String projectId) {
        return buildResponseSpec(HttpStatus.SC_BAD_REQUEST, "Project ID \"%s\" is already used by another project", projectId);
    }

    public static ResponseSpecification checkProjectNameEmpty() {
        return buildResponseSpec(HttpStatus.SC_BAD_REQUEST, "Project name cannot be empty");
    }

    public static ResponseSpecification checkProjectNameId() {
        return buildResponseSpec(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Project ID must not be empty");
    }

    public static ResponseSpecification checkProjectIdInvalid(String invalidProjectId) {
        return buildResponseSpec(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Project ID \"%s\" is invalid: contains non-latin letter 'С'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)", invalidProjectId);
    }

    public static ResponseSpecification checkProjectIdUndersore(String invalidProjectId) {
        return buildResponseSpec(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Project ID \"%s\" is invalid: starts with non-letter character '_'.", invalidProjectId);
    }

    public static ResponseSpecification checkProjectNameLong(String invalidProjectId) {
        return buildResponseSpec(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Project ID \"%s\" is invalid: it is 240 characters long while the maximum length is 225. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters).", invalidProjectId);
    }

    public static ResponseSpecification checkProjectNotFound(String locator) {
        return buildResponseSpec(HttpStatus.SC_NOT_FOUND, "No project found by name or internal/external id '%s'.", locator);
    }

    public static ResponseSpecification checkSubprojectCanNotBeCreatedByCertainRoles() {
        return buildResponseSpec(HttpStatus.SC_FORBIDDEN, "You do not have \"Create subproject\" permission in project with internal id: _Root");
    }
}
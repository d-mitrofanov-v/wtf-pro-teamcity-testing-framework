package com.teamcity.api;

import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.Project;
import com.teamcity.api.requests.CheckedRequests;
import com.teamcity.api.requests.UncheckedRequests;
import com.teamcity.api.requests.unchecked.UncheckedBase;
import com.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.teamcity.api.enums.Endpoint.*;
import static com.teamcity.api.enums.Endpoint.BUILT_TYPES;
import static com.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest {
    @Test(description = "User should be able to create project", groups = {"Positive", "CRUD"})
    public void userCreatesProject() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(user));

        var project = testData.getProject();
        var createdProject = userCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        softy.assertEquals(project.getName(), createdProject.getName(), "Project name is not correct");
    }

    @Test(description = "User should not be able to create two projects with the same name", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithTheSameName() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(user));
        var userUncheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        var project = testData.getProject();
        var projectWithSameName = generate(Arrays.asList(project), Project.class, project.getName());

        userCheckedRequests.<Project>getRequest(PROJECTS).create(project);
        var response = userUncheckedRequests.getRequest(PROJECTS).create(projectWithSameName);

        softy.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
        softy.assertTrue(response.body().asString().contains("Project with this name already exists: %s".formatted(project.getName())));
    }

}

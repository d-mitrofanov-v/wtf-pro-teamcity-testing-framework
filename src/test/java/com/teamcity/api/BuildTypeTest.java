package com.teamcity.api;

import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.Project;
import com.teamcity.api.requests.CheckedRequests;
import com.teamcity.api.requests.unchecked.UncheckedBase;
import com.teamcity.api.spec.Specs;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.teamcity.api.enums.Endpoint.*;
import static com.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specs.authSpec(user));

        userCheckedRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var buildType = testData.getBuildType();
        userCheckedRequests.getRequest(BUILT_TYPES).create(buildType);
        var createdBuildType = userCheckedRequests.<BuildType>getRequest(BUILT_TYPES).read(buildType.getId());
        softy.assertEquals(buildType.getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specs.authSpec(user));

        var project = testData.getProject();
        userCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        var buildType = testData.getBuildType();
        var buildTypeWithSameId = generate(Arrays.asList(project), BuildType.class, buildType.getId());

        userCheckedRequests.getRequest(BUILT_TYPES).create(buildType);
        new UncheckedBase(Specs.authSpec(user), BUILT_TYPES)
                .create(buildTypeWithSameId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(buildType.getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        step("Create user");
        step("Create project");
        step("Grant user PROJECT_ADMIN role in project");

        step("Create buildType for project by user (PROJECT_ADMIN)");
        step("Check buildType was created successfully");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        step("Create user1");
        step("Create project1");
        step("Grant user1 PROJECT_ADMIN role in project1");

        step("Create user2");
        step("Create project2");
        step("Grant user2 PROJECT_ADMIN role in project2");

        step("Create buildType for project1 by user2");
        step("Check buildType was not created with forbidden code");
    }
}
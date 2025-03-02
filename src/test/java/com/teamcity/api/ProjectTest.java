package com.teamcity.api;

import com.teamcity.api.models.Locator;
import com.teamcity.api.models.Project;
import com.teamcity.api.models.Role;
import com.teamcity.api.models.Roles;
import com.teamcity.api.requests.CheckedRequests;
import com.teamcity.api.requests.UncheckedRequests;
import com.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest {

    @Test(description = "Project can be created by Super user", groups = {"Positive", "CRUD"})
    public void superUserCreatesProject() {
        var project = testData.getProject();
        var createdProject = superUserCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        softy.assertEquals(project, createdProject, "Created project doesn't match test project");
    }

    @Test(description = "Project can be created by user", groups = {"Positive", "CRUD"})
    public void userCreatesProject() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(user));

        var project = testData.getProject();
        var createdProject = userCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        softy.assertEquals(project.getName(), createdProject.getName(), "Project name is not correct");
        softy.assertEquals(project.getId(), createdProject.getId(), "Project id is not correct");
    }

    @Test(description = "Project with 'copyAllAssociatedSettings' = false can be created by user", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithCopyAllAssociatedSettingsFalse() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(user));

        var project = generate(Project.class);
        project.setCopyAllAssociatedSettings(false);
        var createdProject = userCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        softy.assertEquals(project.getName(), createdProject.getName(), "Project name is not correct");
        softy.assertEquals(project.getId(), createdProject.getId(), "Project id is not correct");
    }

    @Test(description = "Project with empty name can not be created by user", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyName() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        var project = testData.getProject();
        project.setName("");

        var response = userCheckedRequests.getRequest(PROJECTS).create(project);

        softy.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
        softy.assertTrue(response.body().asString().contains("Project name cannot be empty"));
    }

    @Test(description = "Project can not be created with already used id", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithSameId() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(user));
        var userUncheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        var project = testData.getProject();
        var projectWithSameId = generate(Arrays.asList(project), Project.class, project.getId());

        userCheckedRequests.<Project>getRequest(PROJECTS).create(project);
        var response = userUncheckedRequests.getRequest(PROJECTS).create(projectWithSameId);

        softy.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
        softy.assertTrue(response.body().asString().contains("Project ID \"%s\" is already used by another project".formatted(project.getId())));
    }

    @Test(description = "Project can not be created with already used name", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithSameName() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specifications.authSpec(user));
        var userUncheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        var project = testData.getProject();
        var projectWithSameName = generate(Project.class);
        projectWithSameName.setName(project.getName());

        userCheckedRequests.<Project>getRequest(PROJECTS).create(project);
        var response = userUncheckedRequests.getRequest(PROJECTS).create(projectWithSameName);

        softy.assertEquals(response.statusCode(), HttpStatus.SC_BAD_REQUEST);
        softy.assertTrue(response.body().asString().contains("Project with this name already exists: %s".formatted(project.getName())));
    }

    @Test(description = "Project can not be created with non-latin id", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithNonLatin() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userUncheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        String invalidProjectId = "СашаЛучшийПреподавательВМире";
        var projectWithInvalidId = generate(Project.class);
        projectWithInvalidId.setId(invalidProjectId);

        var response = userUncheckedRequests.getRequest(PROJECTS).create(projectWithInvalidId);
        // Ужас 500ка :(
        softy.assertEquals(response.statusCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        softy.assertTrue(response.body().asString().contains("Project ID \"%s\" is invalid: contains non-latin letter 'С'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters)".formatted(invalidProjectId)));
    }

    @Test(description = "Project can not be created with id bigger then 225 symbols", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithLongId() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userUncheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        String invalidProjectId = "BeepBoop".repeat(30);
        var projectWithInvalidId = generate(Project.class);
        projectWithInvalidId.setId(invalidProjectId);

        var response = userUncheckedRequests.getRequest(PROJECTS).create(projectWithInvalidId);

        softy.assertEquals(response.statusCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        softy.assertTrue(response.body().asString().contains("Project ID \"%s\" is invalid: it is 240 characters long while the maximum length is 225. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters).".formatted(invalidProjectId)));
    }

    @Test(description = "Project can not be copies from non existing project", groups = {"Negative", "Copy"})
    public void userCopiesProjectWithNonExistentLocator() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userUncheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        Locator nonExistingLocator = new Locator("nonExisting");
        var projectWithNonExistingLocator = generate(Project.class);
        projectWithNonExistingLocator.setParentProject(nonExistingLocator);

        var response = userUncheckedRequests.getRequest(PROJECTS).create(projectWithNonExistingLocator);

        softy.assertEquals(response.statusCode(), HttpStatus.SC_NOT_FOUND);
        softy.assertTrue(response.body().asString().contains("No project found by name or internal/external id '%s'.".formatted(nonExistingLocator.getLocator())));
    }

    @Test(description = "Project can not be created by user with certain roles", groups = {"Roles", "Negative", "CRUD"})
    public void nonAdminUserCreatesProject() {
        var roleViewer = Role.builder().roleId("PROJECT_VIEWER").scope("g").build();
        var roleDeveloper = Role.builder().roleId("PROJECT_DEVELOPER").scope("g").build();
        var roleAgentManager = Role.builder().roleId("AGENT_MANAGER").scope("g").build();
        var roles = Roles.builder().role(Arrays.asList(roleViewer, roleDeveloper, roleAgentManager)).build();

        var user = testData.getUser();
        user.setRoles(roles);
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userUncheckedRequests = new UncheckedRequests(Specifications.authSpec(user));

        var response = userUncheckedRequests.getRequest(PROJECTS).create(testData.getProject());

        softy.assertEquals(response.statusCode(), HttpStatus.SC_FORBIDDEN);
        softy.assertTrue(response.body().asString().contains("You do not have \"Create subproject\" permission in project with internal id: _Root"));
    }

}

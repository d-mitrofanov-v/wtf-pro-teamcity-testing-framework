package com.teamcity.api;

import com.teamcity.api.models.*;
import com.teamcity.api.requests.CheckedRequests;
import com.teamcity.api.requests.UncheckedRequests;
import com.teamcity.api.spec.Specs;
import com.teamcity.api.spec.ValidationResponseSpecs;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.teamcity.api.enums.Endpoint.PROJECTS;
import static com.teamcity.api.enums.Endpoint.USERS;
import static com.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest {
    private UncheckedRequests getUserUncheckedRequests(User user) {
        superUserCheckedRequests.getRequest(USERS).create(user);
        return new UncheckedRequests(Specs.authSpec(user));
    }

    private CheckedRequests getUserCheckedRequests(User user) {
        superUserCheckedRequests.getRequest(USERS).create(user);
        return new CheckedRequests(Specs.authSpec(user));
    }

    @Test(description = "Project can be created by Super user", groups = {"Positive", "CRUD"})
    public void superUserCreatesProject() {
        var project = testData.getProject();
        var createdProject = superUserCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        softy.assertEquals(project, createdProject, "Created project doesn't match test project");
    }

    @Test(description = "Project can be created by user", groups = {"Positive", "CRUD"})
    public void userCreatesProject() {
        var userCheckedRequests = getUserCheckedRequests(testData.getUser());

        var project = testData.getProject();
        var createdProject = userCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        softy.assertEquals(project, createdProject, "Created project doesn't match test project");
    }

    @Test(description = "Project with 'copyAllAssociatedSettings' = false can be created by user", groups = {"Positive", "CRUD"})
    public void userCreatesProjectWithCopyAllAssociatedSettingsFalse() {
        var userCheckedRequests = getUserCheckedRequests(testData.getUser());

        var project = generate(Project.class);
        project.setCopyAllAssociatedSettings(false);
        var createdProject = userCheckedRequests.<Project>getRequest(PROJECTS).create(project);
        project.setCopyAllAssociatedSettings(true);

        softy.assertEquals(project, createdProject, "Created project doesn't match test project");
    }

    @Test(description = "Project can not be created with already used id", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithSameId() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specs.authSpec(user));
        var userUncheckedRequests = new UncheckedRequests(Specs.authSpec(user));

        var project = testData.getProject();
        var projectWithSameId = generate(Arrays.asList(project), Project.class, project.getId());

        userCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        userUncheckedRequests.getRequest(PROJECTS)
                .create(projectWithSameId)
                .then()
                .spec(ValidationResponseSpecs.checkProjectWithIdAlreadyExist(project.getId()));
    }

    @Test(description = "Project can not be created with already used name", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithSameName() {
        var user = testData.getUser();
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userCheckedRequests = new CheckedRequests(Specs.authSpec(user));
        var userUncheckedRequests = new UncheckedRequests(Specs.authSpec(user));

        var project = testData.getProject();
        var projectWithSameName = generate(Project.class);
        projectWithSameName.setName(project.getName());

        userCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        userUncheckedRequests.getRequest(PROJECTS)
                .create(projectWithSameName)
                .then()
                .spec(ValidationResponseSpecs.checkProjectWithNameAlreadyExist(project.getName()));
    }

    @Test(description = "Project with empty id can not be created by user", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyId() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        var project = testData.getProject();
        project.setId("");

        userUncheckedRequests.getRequest(PROJECTS)
                .create(project)
                .then()
                .spec(ValidationResponseSpecs.checkProjectNameId());
    }

    @Test(description = "Project with empty name can not be created by user", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyName() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        var project = testData.getProject();
        project.setName("");

        userUncheckedRequests.getRequest(PROJECTS)
                .create(project)
                .then()
                .spec(ValidationResponseSpecs.checkProjectNameEmpty());
    }

    @Test(description = "Project with empty name and empty id can not be created by user", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyNameAndEmptyId() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        var project = testData.getProject();
        project.setId("");
        project.setName("");

        userUncheckedRequests.getRequest(PROJECTS)
                .create(project)
                .then()
                .spec(ValidationResponseSpecs.checkProjectNameEmpty());
    }

    @Test(description = "Project with empty name and invalid id can not be created by user", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithEmptyNameAndInvalidId() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        String invalidProjectId = "Бип-буп";
        var project = testData.getProject();
        project.setId(invalidProjectId);
        project.setName("");

        userUncheckedRequests.getRequest(PROJECTS)
                .create(project)
                .then()
                .spec(ValidationResponseSpecs.checkProjectNameEmpty());
    }

    @Test(description = "Project can not be created with underscore in the beginning of id", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithUnderscoredId() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        String invalidProjectId = "_ProjectID";
        var projectWithInvalidId = generate(Project.class);
        projectWithInvalidId.setId(invalidProjectId);

        userUncheckedRequests.getRequest(PROJECTS)
                .create(projectWithInvalidId)
                .then()
                .spec(ValidationResponseSpecs.checkProjectIdUndersore(invalidProjectId));
    }


    @Test(description = "Project can not be created with non-latin id", groups = {"Negative", "CRUD"})
    public void userCreatesProjectWithNonLatin() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        String invalidProjectId = "СашаЛучшийПреподавательВМире";
        var projectWithInvalidId = generate(Project.class);
        projectWithInvalidId.setId(invalidProjectId);

        userUncheckedRequests.getRequest(PROJECTS)
                .create(projectWithInvalidId)
                .then()
                .spec(ValidationResponseSpecs.checkProjectIdInvalid(invalidProjectId));
    }

    @Test(description = "Project can not be created with id bigger then 225 symbols", groups = {"Negative", "CRUD"}
    )
    public void userCreatesProjectWithLongId() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        String invalidProjectId = "BeepBoop".repeat(30);
        var projectWithInvalidId = generate(Project.class);
        projectWithInvalidId.setId(invalidProjectId);

        userUncheckedRequests.getRequest(PROJECTS)
                .create(projectWithInvalidId)
                .then()
                .spec(ValidationResponseSpecs.checkProjectNameLong(invalidProjectId));

    }

    @Test(description = "Project can not be copies from non existing project", groups = {"Negative", "Copy"})
    public void userCopiesProjectWithNonExistentLocator() {
        var userUncheckedRequests = getUserUncheckedRequests(testData.getUser());

        Locator nonExistingLocator = new Locator("nonExisting");
        var projectWithNonExistingLocator = generate(Project.class);
        projectWithNonExistingLocator.setParentProject(nonExistingLocator);

        userUncheckedRequests.getRequest(PROJECTS)
                .create(projectWithNonExistingLocator)
                .then()
                .spec(ValidationResponseSpecs.checkProjectNotFound(nonExistingLocator.getLocator()));
    }

    @Test(description = "Project can not be created by user with certain roles", groups = {"Negative", "Roles"})
    public void nonAdminUserCreatesProject() {
        var roleViewer = Role.builder().roleId("PROJECT_VIEWER").scope("g").build();
        var roleDeveloper = Role.builder().roleId("PROJECT_DEVELOPER").scope("g").build();
        var roleAgentManager = Role.builder().roleId("AGENT_MANAGER").scope("g").build();
        var roles = Roles.builder().role(Arrays.asList(roleViewer, roleDeveloper, roleAgentManager)).build();

        var user = testData.getUser();
        user.setRoles(roles);
        superUserCheckedRequests.getRequest(USERS).create(user);
        var userUncheckedRequests = new UncheckedRequests(Specs.authSpec(user));

        userUncheckedRequests.getRequest(PROJECTS)
                .create(testData.getProject())
                .then()
                .spec(ValidationResponseSpecs.checkSubprojectCanNotBeCreatedByCertainRoles());
    }

}

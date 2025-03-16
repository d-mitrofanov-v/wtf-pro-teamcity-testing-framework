package com.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.models.Project;
import com.teamcity.ui.pages.ProjectPage;
import com.teamcity.ui.pages.ProjectsPage;
import com.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreatesProject() {
        loginAs(testData.getUser());

        var projectName = testData.getProject().getName();
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(projectName, testData.getBuildType().getName());

        var createdProject = superUserCheckedRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + projectName);
        softy.assertNotNull(createdProject);

        ProjectPage.open(createdProject.getId())
                .pageTitle.shouldHave(Condition.exactText(projectName));

        var projectExists = ProjectsPage.open()
                .getProjects()
                .stream()
                .anyMatch(project -> project.getName().text().equals(projectName));

        softy.assertTrue(projectExists);
    }

    @Test(description = "User should not be able to create a project without name", groups = {"Negative"})
    public void userCreatesProjectWithoutName() {
        step("Login as user");
        step("Check number of projects");

        // взаимодействие с UI
        step("Open `Create Project Page` (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository URL)");
        step("Click `Proceed`");
        step("Set Project Name");
        step("Click `Proceed`");

        // проверка состояния API
        // (корректность отправки данных с UI на API)
        step("Check that number of projects did not change");

        // проверка состояния UI
        // (корректность считывания данных и отображение данных на UI)
        step("Check that error appears `Project name must not be empty`");
    }
}

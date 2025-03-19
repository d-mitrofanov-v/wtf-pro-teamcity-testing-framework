package com.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.teamcity.api.enums.Endpoint;
import com.teamcity.api.models.BuildType;
import com.teamcity.api.models.Project;
import com.teamcity.ui.errors.UiErrors;
import com.teamcity.ui.pages.BuildTypePage;
import com.teamcity.ui.pages.ProjectPage;
import com.teamcity.ui.pages.admin.CreateBuildTypePage;
import com.teamcity.ui.validators.ValidateElement;
import org.testng.annotations.Test;

import static com.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class CreateBuildTypeTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create buildType", groups = {"Positive"})
    public void userCreatesBuildType() {
        var project = testData.getProject();
        superUserCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        loginAs(testData.getUser());

        var buildTypeName = testData.getBuildType().getName();
        CreateBuildTypePage.open(project.getId())
                .createForm(REPO_URL)
                .setupBuildType(buildTypeName);

        var createdBuildType = superUserCheckedRequests.<BuildType>getRequest(Endpoint.BUILT_TYPES).read("name:" + buildTypeName);
        softy.assertNotNull(createdBuildType);

        BuildTypePage.open(createdBuildType.getId())
                .pageTitle.shouldHave(Condition.exactText(buildTypeName));

        var buildTypeExists = ProjectPage.open(project.getId())
                .getBuildTypes()
                .stream()
                .anyMatch(buildType -> buildType.getName().text().equals(createdBuildType.getName()));

        softy.assertTrue(buildTypeExists);
    }

    @Test(description = "User should not be able to create buildType with empty Repo URL", groups = {"Negative"})
    public void userCreatesBuildTypeWithEmptyRepoURL(){
        var project = testData.getProject();
        superUserCheckedRequests.<Project>getRequest(PROJECTS).create(project);

        loginAs(testData.getUser());

        var valError = CreateBuildTypePage.open(project.getId())
                .setupWithoutURL()
                .getValidationError();

        ValidateElement.byText(valError, UiErrors.BUILD_CONFIG_URL_MUST_BE_NOT_NULL, softy);
    }
}

package com.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.ui.elements.BuildTypeElement;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectPage extends BasePage {
    private static final String PROJECT_URL = "/project/%s";
    private final ElementsCollection buildTypeElements = $$("div[class*='BuildsByBuildType__container']");
    public SelenideElement pageTitle = $("span[class='ProjectPageHeader__title--ih']");

    @Step("Open project page")
    public static ProjectPage open(String projectId) {
        return Selenide.open(PROJECT_URL.formatted(projectId), ProjectPage.class);
    }

    public List<BuildTypeElement> getBuildTypes() {
        pageTitle.should(Condition.appear, BASE_WAITING);
        return generatePageElements(buildTypeElements, BuildTypeElement::new);
    }
}


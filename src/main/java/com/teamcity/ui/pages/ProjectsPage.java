package com.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.ui.elements.ProjectElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectsPage extends BasePage {
    private static final String PROJECTS_URL = "/favorite/projects";
    private final ElementsCollection projectElements = $$("div[class*='Subproject__container']");

    public ProjectsPage() {
        SelenideElement header = $(".MainPanel__router--gF > div");
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    public List<ProjectElement> getProjects() {
        return generatePageElements(projectElements, ProjectElement::new);
    }
}

package com.teamcity.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.ui.elements.BasePageElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class BasePage {
    protected static final Duration BASE_WAITING = Duration.ofSeconds(60);

    protected <T extends BasePageElement> List<T> generatePageElements(ElementsCollection collection, Function<SelenideElement, T> creator) {
        return collection.stream().map(creator).toList();
    }
}

package com.teamcity.ui.validators;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.testng.asserts.SoftAssert;

public class ValidateElement {
    public static void byText(SelenideElement element, String expectedText, SoftAssert softy) {
        element.shouldBe(Condition.visible);
        softy.assertTrue(element.has(Condition.exactText(expectedText)));
    }
}

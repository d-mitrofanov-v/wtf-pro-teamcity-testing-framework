package com.teamcity.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.teamcity.api.models.User;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage {
    private static final String LOGIN_URL = "/login.html";
    private final SelenideElement inputUsername = $("#username");
    private final SelenideElement inputPassword = $("#password");
    private final SelenideElement buttonLogin = $(".loginButton");

    public static LoginPage open() {
        return Selenide.open(LOGIN_URL, LoginPage.class);
    }

    public ProjectPage login(User user) {
        inputUsername.val(user.getUsername());
        inputPassword.val(user.getPassword());
        buttonLogin.click();

        return Selenide.page(ProjectPage.class);
    }
}

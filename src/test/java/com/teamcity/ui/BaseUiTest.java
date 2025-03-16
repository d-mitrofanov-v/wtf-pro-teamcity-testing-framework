package com.teamcity.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.teamcity.BaseTest;
import com.teamcity.api.config.Config;
import com.teamcity.api.models.User;
import com.teamcity.ui.pages.LoginPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

import java.util.Map;

import static com.teamcity.api.enums.Endpoint.USERS;

public class BaseUiTest extends BaseTest {
    @BeforeSuite(alwaysRun = true)
    public void setupUiTest() {
        Configuration.browser = Config.getProperty("browser");
        Configuration.remote = Config.getProperty("remote");
        Configuration.baseUrl = "http://" + Config.getProperty("host");
        Configuration.browserSize = Config.getProperty("browserSize");

        Configuration.browserCapabilities.setCapability(
                "selenoid:options", Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @AfterMethod(alwaysRun = true)
    public void closeWebDriver() {
        Selenide.closeWebDriver();
    }

    protected void loginAs(User user) {
        superUserCheckedRequests.getRequest(USERS).create(user);
        LoginPage.open().login(user);
    }
}

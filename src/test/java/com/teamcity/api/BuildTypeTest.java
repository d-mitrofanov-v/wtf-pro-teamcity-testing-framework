package com.teamcity.api;

import com.teamcity.api.models.User;
import com.teamcity.api.spec.Specifications;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Regression"})
    public void UserCreatesBuildTypeTest() {
        step("");

    }

    //@Test
    //public void userShouldBeAbleGetAllProjects() {
    //    RestAssured
    //            .given()
    //            .spec(Specifications.getSpec().authSpec(User.builder().user("admin").password("admin").build()))
    //            .get("/app/rest/projects");
    //
    //}
}

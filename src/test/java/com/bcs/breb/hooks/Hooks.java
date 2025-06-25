package com.bcs.breb.hooks;

import io.cucumber.java.Before;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.openqa.selenium.WebDriver;

public class Hooks {

    @Managed
    private WebDriver navegador;

    @Before
    public void setThestage(){
        OnStage.setTheStage(
            OnlineCast.whereEveryoneCan(
                BrowseTheWeb.with(navegador)
            )
        );
        OnStage.theActorCalled("actor");
    }
}

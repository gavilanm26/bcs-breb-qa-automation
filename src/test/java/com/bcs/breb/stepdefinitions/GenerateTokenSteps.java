package com.bcs.breb.stepdefinitions;

import com.bcs.breb.exceptions.DigitalToken;
import com.bcs.breb.exceptions.ResponseCodeIs;
import com.bcs.breb.tasks.GenerateToken;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import net.serenitybdd.screenplay.GivenWhenThen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

public class GenerateTokenSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateTokenSteps.class);


    @Given("^que el usuario viene autenticado desde la app móvil para generar un token digital$")
    public void queElUsuarioVieneDesdeLaAppMovilParaGenerarUnToken() {
        LOGGER.info("El actor genera un token digital.");
    }


    @When("^consume el servicio de token con los siguientes datos: (.*), (.*), (.*), (.*)$")
    public void consumeElServicioDeTokenConLosSiguientesDatos(String documentType, String documentNumber, String firstName, String appId) {
        theActorInTheSpotlight().wasAbleTo(
            GenerateToken.with(documentType, documentNumber, firstName, appId)
        );
    }

    @Then("^la respuesta debe ser exitosa$")
    public void laRespuestaDebeSerExitosa() {
        theActorInTheSpotlight().should(
            GivenWhenThen.seeThat(
                ResponseCodeIs.ok()
            )
        );
    }

    @And("^guarda el token para usos posteriores$")
    public void guardaElTokenParaUsosPosteriores() {
        String token = theActorInTheSpotlight().asksFor(DigitalToken.value());
        theActorInTheSpotlight().remember("jwt_token", token);
    }
}

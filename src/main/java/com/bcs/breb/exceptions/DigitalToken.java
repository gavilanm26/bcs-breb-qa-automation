package com.bcs.breb.exceptions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalToken implements Question<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DigitalToken.class);

    @Override
    public String answeredBy(Actor actor) {
        String token = SerenityRest.lastResponse().jsonPath().getString("token");

        LOGGER.info("🔐 Token capturado: {}", token);

        return token;
    }

    public static DigitalToken value() {
        return new DigitalToken();
    }
}
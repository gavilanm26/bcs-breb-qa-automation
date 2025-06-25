package com.bcs.breb.exceptions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ResponseCodeIs implements Question<Boolean> {

    private final int expectedStatus;

    public ResponseCodeIs(int expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return SerenityRest.lastResponse().statusCode() == expectedStatus;
    }

    public static ResponseCodeIs ok() {
        return new ResponseCodeIs(200);
    }
}

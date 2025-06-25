package com.bcs.breb.tasks;

import com.bcs.breb.utils.config.ConfigReader;
import com.bcs.breb.utils.crypto.Crypto;
import com.bcs.breb.utils.http.MtlsRestClient;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.Actor;

import static io.restassured.config.RestAssuredConfig.config;

public class GenerateToken implements Task {

    private final String documentType;
    private final String documentNumber;
    private final String firstName;
    private final String appId;

    private static final String IP         = "192.168.168.47";
    private static final String PROCESS_ID = "APIUCDT000000005011";
    private static final String USER       = "CC1023863946";
    private static final String CHANNEL    = "07";
    private static final String SUBCHANNEL = "10";

    public GenerateToken(String documentType, String documentNumber, String firstName, String appId) {
        this.documentType   = documentType;
        this.documentNumber = documentNumber;
        this.firstName      = firstName;
        this.appId          = appId;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // 1) Configurar RestAssured con nuestro cliente MTLS
        RestAssured.config = config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .httpClientFactory(MtlsRestClient::build));

        // 2) Payload plano y cifrado
        String payload = String.format(
                "{\"documentType\":\"%s\",\"documentNumber\":\"%s\",\"firstName\":\"%s\",\"appId\":\"%s\"}",
                documentType, documentNumber, firstName, appId
        );

        String encryptedBody = Crypto.encrypt(payload);

        String baseUrl      = ConfigReader.get("app.base.url");
        String clientId     = ConfigReader.get("client.id");
        String clientSecret = ConfigReader.get("client.secret");
        String grantType    = ConfigReader.get("grant.type");
        String url = baseUrl + "/api-gateway-mtls/v1/generate-digital-token";

        SerenityRest.given()
                .relaxedHTTPSValidation()
                .config(RestAssured.config)
                .header("Content-Type", "application/json")
                .header("client_id",     clientId)
                .header("client_secret", clientSecret)
                .header("grant_type",    grantType)
                .header("x-invoker-useripaddress", IP)
                .header("X-Invoker-ProcessId",      PROCESS_ID)
                .header("x-invoker-user",           USER)
                .header("x-invoker-channel",        CHANNEL)
                .header("x-invoker-subchannel",     SUBCHANNEL)
                .body("{ \"data\": \"" + encryptedBody + "\" }")
                .post(url)
                .then()
                .log().all()
                .statusCode(200);
    }

    public static GenerateToken with(String documentType, String documentNumber, String firstName, String appId) {
        return Tasks.instrumented(GenerateToken.class, documentType, documentNumber, firstName, appId);
    }
}

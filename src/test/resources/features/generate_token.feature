Feature: Generación de token digital

  As: un consumidor del API MTLS
  I want: cifrar y enviar los datos personales del usuario
  To: obtener un token digital exitosamente

  @successToken @api @regression
  Scenario Outline: Generar token exitoso con datos correctos
    Given que el usuario viene autenticado desde la app móvil para generar un token digital
    When consume el servicio de token con los siguientes datos: <documentType>, <documentNumber>, <firstName>, <appId>
    Then la respuesta debe ser exitosa
    And guarda el token para usos posteriores

    Examples:
      | documentType | documentNumber | firstName | appId |
            ##@externaldata@./src/test/resources/data/token_data.xlsx@Data
|CC|1004549025|Mauro|breb|
|CE|1004549026|Mauro|breb|
|PE|1004549027|Mauro|breb|




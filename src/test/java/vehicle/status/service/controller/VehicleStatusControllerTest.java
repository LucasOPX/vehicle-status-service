package vehicle.status.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;
import vehicle.status.service.model.enums.Feature;

import java.util.Arrays;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleStatusControllerTest implements TestPropertyProvider {

    @Inject
    @Client("/")
    io.micronaut.http.client.HttpClient client;

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> getProperties() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        String baseUrl = "http://localhost:" + wireMockServer.port();
        return Map.of(
                "insurance.service.url", baseUrl,
                "maintenance.service.url",  baseUrl,
                "micronaut.http.client.read-timeout", "40s"
        );
    }

    @BeforeAll
    void setupMocks() {
        configureStubs();
    }

    @AfterAll
    void cleanup() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private void configureStubs() {
        // Stub for insurance service
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/accidents/report"))
                .withQueryParam("vin", equalTo("4Y1SL65848Z411439"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"report\": { \"claims\": 0 } }")
                        .withHeader("Content-Type", "application/json")));

        // Stub for maintenance service
        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/cars/4Y1SL65848Z411439"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"maintenance_frequency\": \"average\" }")
                        .withHeader("Content-Type", "application/json")));

        wireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/accidents/report"))
                .withQueryParam("vin", equalTo("INVALIDVIN"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{ \"error\": \"Invalid VIN\" }")
                        .withHeader("Content-Type", "application/json")));
    }

    @Test
    void testCheckStatus_InvalidVin() {
        VehicleStatusRequest requestBody = new VehicleStatusRequest();
        requestBody.setVin("INVALIDVIN");
        requestBody.setFeatures(Arrays.asList(Feature.ACCIDENT_FREE));

        HttpRequest<VehicleStatusRequest> request = HttpRequest.POST("/status/check", requestBody);

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, VehicleStatusResponse.class));

        assertEquals(400, exception.getStatus().getCode());
    }

    @Test
    void testSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        VehicleStatusResponse response = VehicleStatusResponse.builder()
                .requestId("12345")
                .vin("4Y1SL65848Z411439")
                .accidentFree(true)
                .maintenanceScore("good")
                .build();

        String json = objectMapper.writeValueAsString(response);
        System.out.println("Serialized JSON: " + json);

        VehicleStatusResponse deserialized = objectMapper.readValue(json, VehicleStatusResponse.class);
        assertEquals(response, deserialized);
    }
}
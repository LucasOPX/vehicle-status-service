package vehicle.status.service.service.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vehicle.status.service.client.InsuranceClient;
import vehicle.status.service.model.InsuranceReport;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AccidentFreeFeatureHandlerTest {

    @Mock
    private InsuranceClient insuranceClient;

    private AccidentFreeFeatureHandler handler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new AccidentFreeFeatureHandler(insuranceClient);
    }

    @Test
    public void testHandle_Success_NoClaims() {
        // Arrange
        VehicleStatusRequest request = new VehicleStatusRequest();
        request.setVin("VIN123456789012345");
        VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder = VehicleStatusResponse.builder();

        InsuranceReport.Report reportData = new InsuranceReport.Report();
        reportData.setClaims(0);
        InsuranceReport report = new InsuranceReport();
        report.setReport(reportData);

        when(insuranceClient.getAccidentReport(anyString(), anyString()))
                .thenReturn(Mono.just(report));

        // Act
        Mono<Void> result = handler.handle(request, responseBuilder, "request-id");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        VehicleStatusResponse response = responseBuilder.build();
        assert response.getAccidentFree();

        verify(insuranceClient).getAccidentReport(anyString(), eq("request-id"));
    }

    @Test
    public void testHandle_Success_WithClaims() {
        // Arrange
        VehicleStatusRequest request = new VehicleStatusRequest();
        request.setVin("VIN123456789012345");
        VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder = VehicleStatusResponse.builder();

        InsuranceReport.Report reportData = new InsuranceReport.Report();
        reportData.setClaims(2);
        InsuranceReport report = new InsuranceReport();
        report.setReport(reportData);

        when(insuranceClient.getAccidentReport(anyString(), anyString()))
                .thenReturn(Mono.just(report));

        // Act
        Mono<Void> result = handler.handle(request, responseBuilder, "request-id");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        VehicleStatusResponse response = responseBuilder.build();
        assert !response.getAccidentFree();

        verify(insuranceClient).getAccidentReport(anyString(), eq("request-id"));
    }

}
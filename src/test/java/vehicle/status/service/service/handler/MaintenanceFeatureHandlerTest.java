package vehicle.status.service.service.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vehicle.status.service.client.MaintenanceClient;
import vehicle.status.service.exception.ServiceException;
import vehicle.status.service.model.MaintenanceInfo;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MaintenanceFeatureHandlerTest {

    @Mock
    private MaintenanceClient maintenanceClient;

    private MaintenanceFeatureHandler handler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new MaintenanceFeatureHandler(maintenanceClient);
    }

    @Test
    public void testHandle_Success_MediumFrequency() {
        // Arrange
        VehicleStatusRequest request = new VehicleStatusRequest();
        request.setVin("VIN123456789012345");
        VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder = VehicleStatusResponse.builder();

        MaintenanceInfo info = new MaintenanceInfo();
        info.setMaintenanceFrequency("medium");

        when(maintenanceClient.getMaintenanceInfo(anyString(), anyString()))
                .thenReturn(Mono.just(info));

        // Act
        Mono<Void> result = handler.handle(request, responseBuilder, "request-id");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        VehicleStatusResponse response = responseBuilder.build();
        assert response.getMaintenanceScore().equals("average");

        verify(maintenanceClient).getMaintenanceInfo(anyString(), eq("request-id"));
    }

    @Test
    public void testHandle_Success_HighFrequency() {
        // Arrange
        VehicleStatusRequest request = new VehicleStatusRequest();
        request.setVin("VIN123456789012345");
        VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder = VehicleStatusResponse.builder();

        MaintenanceInfo info = new MaintenanceInfo();
        info.setMaintenanceFrequency("high");

        when(maintenanceClient.getMaintenanceInfo(anyString(), anyString()))
                .thenReturn(Mono.just(info));

        // Act
        Mono<Void> result = handler.handle(request, responseBuilder, "request-id");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        VehicleStatusResponse response = responseBuilder.build();
        assert response.getMaintenanceScore().equals("good");

        verify(maintenanceClient).getMaintenanceInfo(anyString(), eq("request-id"));
    }

    @Test
    public void testHandle_UnknownFrequency() {
        // Arrange
        VehicleStatusRequest request = new VehicleStatusRequest();
        request.setVin("VIN123456789012345");
        VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder = VehicleStatusResponse.builder();

        MaintenanceInfo info = new MaintenanceInfo();
        info.setMaintenanceFrequency("unknown");

        when(maintenanceClient.getMaintenanceInfo(anyString(), anyString()))
                .thenReturn(Mono.just(info));

        // Act
        Mono<Void> result = handler.handle(request, responseBuilder, "request-id");

        // Assert
        StepVerifier.create(result)
                .expectError(ServiceException.class)
                .verify();

        verify(maintenanceClient).getMaintenanceInfo(anyString(), eq("request-id"));
    }

}
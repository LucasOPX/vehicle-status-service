package vehicle.status.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vehicle.status.service.exception.ServiceException;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;
import vehicle.status.service.model.enums.Feature;
import vehicle.status.service.service.handler.FeatureHandler;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class VehicleStatusServiceImplTest {

    @Mock
    private FeatureHandler accidentFreeHandler;

    @Mock
    private FeatureHandler maintenanceHandler;

    private VehicleStatusService vehicleStatusService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(accidentFreeHandler.supports(Feature.ACCIDENT_FREE)).thenReturn(true);
        when(maintenanceHandler.supports(Feature.MAINTENANCE)).thenReturn(true);

        vehicleStatusService = new VehicleStatusServiceImpl(
                Arrays.asList(accidentFreeHandler, maintenanceHandler)
        );
    }

    @Test
    public void testCheckStatus_Success() {
        // Arrange
        VehicleStatusRequest request = new VehicleStatusRequest();
        request.setVin("4Y1SL65848Z411439");
        request.setFeatures(Arrays.asList(Feature.ACCIDENT_FREE, Feature.MAINTENANCE));

        when(accidentFreeHandler.handle(any(), any(), anyString())).thenReturn(Mono.empty());
        when(maintenanceHandler.handle(any(), any(), anyString())).thenReturn(Mono.empty());

        // Act
        Mono<VehicleStatusResponse> result = vehicleStatusService.checkStatus(request, "request-id");

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getRequestId().equals("request-id");
                    assert response.getVin().equals("4Y1SL65848Z411439");
                })
                .verifyComplete();

        verify(accidentFreeHandler).handle(any(), any(), eq("request-id"));
        verify(maintenanceHandler).handle(any(), any(), eq("request-id"));
    }

    @Test
    public void testCheckStatus_FeatureHandlerThrowsException() {
        // Arrange
        VehicleStatusRequest request = new VehicleStatusRequest();
        request.setVin("4Y1SL65848Z411439");
        request.setFeatures(Collections.singletonList(Feature.ACCIDENT_FREE));

        when(accidentFreeHandler.handle(any(), any(), anyString()))
                .thenReturn(Mono.error(new ServiceException("Handler error")));

        // Act
        Mono<VehicleStatusResponse> result = vehicleStatusService.checkStatus(request, "request-id");

        // Assert
        StepVerifier.create(result)
                .expectError(ServiceException.class)
                .verify();

        verify(accidentFreeHandler).handle(any(), any(), eq("request-id"));
        verifyNoInteractions(maintenanceHandler);
    }

}

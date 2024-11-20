package vehicle.status.service.service;

import reactor.core.publisher.Mono;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;

public interface VehicleStatusService {
    Mono<VehicleStatusResponse> checkStatus(VehicleStatusRequest request, String requestId);
}

package vehicle.status.service.service.handler;

import reactor.core.publisher.Mono;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;
import vehicle.status.service.model.enums.Feature;

public interface FeatureHandler {
    boolean supports(Feature feature);
    Mono<Void> handle(VehicleStatusRequest request, VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder, String requestId);
}

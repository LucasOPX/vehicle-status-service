package vehicle.status.service.client;

import reactor.core.publisher.Mono;
import vehicle.status.service.model.MaintenanceInfo;

public interface MaintenanceClient {
    Mono<MaintenanceInfo> getMaintenanceInfo(String vin, String requestId);
}

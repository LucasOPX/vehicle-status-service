package vehicle.status.service.client;

import reactor.core.publisher.Mono;
import vehicle.status.service.model.InsuranceReport;

public interface InsuranceClient {
    Mono<InsuranceReport> getAccidentReport(String vin, String requestId);
}
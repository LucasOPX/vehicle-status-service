package vehicle.status.service.client;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import vehicle.status.service.exception.ThirdPartyServiceException;
import vehicle.status.service.model.MaintenanceInfo;

@Singleton
public class MaintenanceClientImpl implements MaintenanceClient {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenanceClientImpl.class);

    private final ReactorHttpClient httpClient;

    @Inject
    public MaintenanceClientImpl(@Client("${maintenance.service.url}") ReactorHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Mono<MaintenanceInfo> getMaintenanceInfo(String vin, String requestId) {
        String uri = "/cars/" + vin;
        LOG.info("[{}] Calling Maintenance Service at {}", requestId, uri);

        HttpRequest<?> httpRequest = HttpRequest.GET(uri);

        return httpClient.retrieve(httpRequest, MaintenanceInfo.class)
                .single()
                .doOnSuccess(response -> LOG.info("[{}] Received response from Maintenance Service", requestId))
                .doOnError(error -> LOG.error("[{}] Maintenance Service call failed: {}", requestId, error.getMessage()))
                .onErrorResume(error -> Mono.error(new ThirdPartyServiceException("Failed to fetch maintenance info", error)));
    }
}

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
import vehicle.status.service.model.InsuranceReport;

@Singleton
public class InsuranceClientImpl implements InsuranceClient {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceClientImpl.class);

    private final ReactorHttpClient httpClient;

    @Inject
    public InsuranceClientImpl(@Client("${insurance.service.url}") ReactorHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Mono<InsuranceReport> getAccidentReport(String vin, String requestId) {
        String uri = "/accidents/report?vin=" + vin;
        LOG.info("[{}] Calling Insurance Service at {}", requestId, uri);

        HttpRequest<?> httpRequest = HttpRequest.GET(uri);

        return httpClient.retrieve(httpRequest, InsuranceReport.class)
                .single()
                .doOnSuccess(response -> LOG.info("[{}] Received response from Insurance Service", requestId))
                .doOnError(error -> LOG.error("[{}] Insurance Service call failed: {}", requestId, error.getMessage()))
                .onErrorResume(error -> Mono.error(new ThirdPartyServiceException("Failed to fetch accident report", error)));
    }
}

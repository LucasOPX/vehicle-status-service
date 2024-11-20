package vehicle.status.service.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import vehicle.status.service.exception.ServiceException;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;
import vehicle.status.service.model.enums.Feature;
import vehicle.status.service.service.handler.FeatureHandler;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class VehicleStatusServiceImpl implements VehicleStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleStatusServiceImpl.class);

    private final List<FeatureHandler> featureHandlers;

    @Inject
    public VehicleStatusServiceImpl(List<FeatureHandler> featureHandlers) {
        this.featureHandlers = featureHandlers;
    }

    @Override
    public Mono<VehicleStatusResponse> checkStatus(VehicleStatusRequest request, String requestId) {
        LOG.info("[{}] Processing request in VehicleStatusService", requestId);

        VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder = VehicleStatusResponse.builder()
                .requestId(requestId)
                .vin(request.getVin());

        List<Mono<Void>> featureMonos = request.getFeatures().stream()
                .map(feature -> getHandlerForFeature(feature)
                        .handle(request, responseBuilder, requestId))
                .collect(Collectors.toList());

        return Mono.when(featureMonos)
                .then(Mono.fromCallable(responseBuilder::build))
                .doOnSuccess(response -> LOG.info("[{}] Successfully processed request", requestId))
                .doOnError(error -> LOG.error("[{}] Error processing request: {}", requestId, error.getMessage()))
                .onErrorResume(error -> Mono.error(new ServiceException("Failed to process request")));
    }

    private FeatureHandler getHandlerForFeature(Feature feature) {
        return featureHandlers.stream()
                .filter(handler -> handler.supports(feature))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Unsupported feature: " + feature));
    }
}

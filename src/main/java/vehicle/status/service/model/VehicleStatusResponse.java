package vehicle.status.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleStatusResponse implements ApiResponse {

    @JsonProperty("request_id")
    private String requestId;

    private String vin;

    @JsonProperty("accident_free")
    private Boolean accidentFree;

    @JsonProperty("maintenance_score")
    private String maintenanceScore; // Possible values: poor, average, good
}

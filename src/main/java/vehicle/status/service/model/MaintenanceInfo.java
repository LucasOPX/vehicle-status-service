package vehicle.status.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MaintenanceInfo {

    @JsonProperty("maintenance_frequency")
    private String maintenanceFrequency; // Possible values: very-low, low, medium, high
}
package vehicle.status.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import vehicle.status.service.model.enums.Feature;

import java.util.List;

public class VehicleStatusRequest {

    public  VehicleStatusRequest() {}

    @JsonCreator
    public VehicleStatusRequest(
            @JsonProperty("vin") String vin,
            @JsonProperty("features") List<Feature> features
    ) {
        this.vin = vin;
        this.features = features;
    }

    @NotBlank(message = "VIN must not be blank")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters long")
    @Pattern(
            regexp = "^[A-HJ-NPR-Z0-9]+$",
            message = "VIN must contain only uppercase letters (except I, O, Q) and digits"
    )
    private String vin;

    @NotNull(message = "Features must not be null")
    @Size(min = 1, message = "At least one feature must be provided")
    private List<Feature> features;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}

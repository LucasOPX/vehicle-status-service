package vehicle.status.service.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Feature {
    ACCIDENT_FREE("accident_free"),
    MAINTENANCE("maintenance");

    private final String featureName;

    Feature(String featureName) {
        this.featureName = featureName;
    }

    @Override
    public String toString() {
        return featureName;
    }

    @JsonValue
    public String getFeatureName() {
        return featureName;
    }
}

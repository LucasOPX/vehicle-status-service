package vehicle.status.service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InsuranceReport {

    private Report report;

    @Data
    @NoArgsConstructor
    public static class Report {
        private int claims;
    }
}
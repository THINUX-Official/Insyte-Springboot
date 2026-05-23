package com.insurance.thinux.insytespringboot.dto.request;

import com.insurance.thinux.insytespringboot.enums.FraudAlertStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class FraudAlertStatusUpdateRequestDTO {

    private FraudAlertStatus status;
    private Long reviewedById;
}

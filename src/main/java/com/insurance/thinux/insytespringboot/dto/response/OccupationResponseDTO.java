package com.insurance.thinux.insytespringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupationResponseDTO {

    private Long id;
    private String code;
    private String name;
}
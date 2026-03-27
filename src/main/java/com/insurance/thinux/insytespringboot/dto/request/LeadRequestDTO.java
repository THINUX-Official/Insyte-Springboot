package com.insurance.thinux.insytespringboot.dto.request;

import com.insurance.thinux.insytespringboot.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeadRequestDTO {
    @NotNull
    private Status status;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 12)
    private String nic;

    private String email;
    private Gender gender;
    private CivilStatus civilStatus;
    private LocalDate dob;
    private String mobile;
    private Long occupationId;
    private Race race;

    private String address1;
    private String address2;
    private String city;
    private District district;
    private Province province;
    private Country country;

    private BigDecimal premium;
    private ProductType productType;
    private LeadSource leadSource;
    private Probability probability;
    private LocalDate remindDate;
    private String remark;
    private String attachmentPath;

    @NotNull
    private Long assignedUserId;
}

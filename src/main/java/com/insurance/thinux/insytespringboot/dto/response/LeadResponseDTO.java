package com.insurance.thinux.insytespringboot.dto.response;

import com.insurance.thinux.insytespringboot.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeadResponseDTO {
    private Long id;
    private LeadStatus status;
    private String name;
    private String nic;
    private String email;
    private Gender gender;
    private CivilStatus civilStatus;
    private LocalDate dob;
    private String mobile;
    private Long occupationId;
    private String occupationCode;
    private String occupationName;
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
    private Long assignedUserId;
    private String assignedUsername;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.insurance.thinux.insytespringboot.dto.response;

import com.insurance.thinux.insytespringboot.enums.RecommendationPriority;
import com.insurance.thinux.insytespringboot.enums.RecommendationStatus;
import com.insurance.thinux.insytespringboot.enums.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecommendationResponseDTO {

    private Long id;

    private Long agentId;
    private String username;
    private String nickname;
    private String email;
    private String phone;

    private Long supervisorId;
    private String supervisorUsername;

    private RecommendationType recommendationType;
    private String title;
    private String recommendationText;

    private RecommendationPriority priority;
    private RecommendationStatus status;

    private LocalDateTime generatedAt;
    private LocalDateTime viewedAt;
}

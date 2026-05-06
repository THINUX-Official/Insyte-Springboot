package com.insurance.thinux.insytespringboot.model;

import com.insurance.thinux.insytespringboot.enums.ChatbotFaqCategory;
import com.insurance.thinux.insytespringboot.enums.CommonStatus;
import com.insurance.thinux.insytespringboot.util.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chatbot_faqs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatbotFaq extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ChatbotFaqCategory category = ChatbotFaqCategory.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CommonStatus status = CommonStatus.ACTIVE;
}

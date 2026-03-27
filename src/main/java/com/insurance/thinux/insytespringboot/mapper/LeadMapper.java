package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.request.LeadRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.LeadResponseDTO;
import com.insurance.thinux.insytespringboot.model.Lead;
import com.insurance.thinux.insytespringboot.model.Occupation;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public Lead toEntity(LeadRequestDTO dto, User user, Occupation occupation) {
        Lead lead = new Lead();
        mapCommonFields(lead, dto, user, occupation);
        return lead;
    }

    public void updateEntity(Lead lead, LeadRequestDTO dto, User user, Occupation occupation) {
        mapCommonFields(lead, dto, user, occupation);
    }

    private void mapCommonFields(Lead lead, LeadRequestDTO dto, User user, Occupation occupation) {
        lead.setStatus(dto.getStatus());
        lead.setName(dto.getName());
        lead.setNic(dto.getNic());
        lead.setEmail(dto.getEmail());
        lead.setGender(dto.getGender());
        lead.setCivilStatus(dto.getCivilStatus());
        lead.setDob(dto.getDob());
        lead.setMobile(dto.getMobile());
        lead.setOccupation(occupation);
        lead.setRace(dto.getRace());
        lead.setAddress1(dto.getAddress1());
        lead.setAddress2(dto.getAddress2());
        lead.setCity(dto.getCity());
        lead.setDistrict(dto.getDistrict());
        lead.setProvince(dto.getProvince());
        lead.setCountry(dto.getCountry());
        lead.setPremium(dto.getPremium());
        lead.setProductType(dto.getProductType());
        lead.setLeadSource(dto.getLeadSource());
        lead.setProbability(dto.getProbability());
        lead.setRemindDate(dto.getRemindDate());
        lead.setRemark(dto.getRemark());
        lead.setAttachmentPath(dto.getAttachmentPath());
        lead.setAssignedUser(user);
    }

    public LeadResponseDTO toDTO(Lead lead) {
        LeadResponseDTO dto = new LeadResponseDTO();

        dto.setId(lead.getId());
        dto.setStatus(lead.getStatus());
        dto.setName(lead.getName());
        dto.setNic(lead.getNic());
        dto.setEmail(lead.getEmail());
        dto.setGender(lead.getGender());
        dto.setCivilStatus(lead.getCivilStatus());
        dto.setDob(lead.getDob());
        dto.setMobile(lead.getMobile());

        if (lead.getOccupation() != null) {
            dto.setOccupationName(lead.getOccupation().getName());
        }

        dto.setRace(lead.getRace());
        dto.setAddress1(lead.getAddress1());
        dto.setAddress2(lead.getAddress2());
        dto.setCity(lead.getCity());
        dto.setDistrict(lead.getDistrict());
        dto.setProvince(lead.getProvince());
        dto.setCountry(lead.getCountry());

        dto.setPremium(lead.getPremium());
        dto.setProductType(lead.getProductType());
        dto.setLeadSource(lead.getLeadSource());
        dto.setProbability(lead.getProbability());

        dto.setRemindDate(lead.getRemindDate());
        dto.setRemark(lead.getRemark());
        dto.setAttachmentPath(lead.getAttachmentPath());

        if (lead.getAssignedUser() != null) {
            dto.setAssignedUserId(lead.getAssignedUser().getId());
            dto.setAssignedUsername(lead.getAssignedUser().getUsername());
        }

        dto.setCreatedAt(lead.getCreatedAt());
        dto.setUpdatedAt(lead.getUpdatedAt());

        return dto;
    }
}

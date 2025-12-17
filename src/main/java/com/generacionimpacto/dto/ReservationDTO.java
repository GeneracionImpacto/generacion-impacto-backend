package com.generacionimpacto.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ReservationDTO {
    
    @NotNull
    private Long announcementId;
    
    @NotNull
    private LocalDateTime dateTime;
    
    public ReservationDTO() {}
    
    // Getters and Setters
    public Long getAnnouncementId() {
        return announcementId;
    }
    
    public void setAnnouncementId(Long announcementId) {
        this.announcementId = announcementId;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}





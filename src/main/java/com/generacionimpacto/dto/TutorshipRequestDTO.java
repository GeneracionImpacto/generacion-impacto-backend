package com.generacionimpacto.dto;

import jakarta.validation.constraints.NotBlank;

public class TutorshipRequestDTO {
    
    @NotBlank
    private String courseName;
    
    @NotBlank
    private String teacherName;
    
    @NotBlank
    private String period;
    
    private String description;
    
    private String videoUrl;
    
    public TutorshipRequestDTO() {}
    
    // Getters and Setters
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}





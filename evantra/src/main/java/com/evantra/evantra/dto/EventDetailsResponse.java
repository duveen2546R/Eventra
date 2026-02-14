package com.evantra.evantra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class EventDetailsResponse {

    private UUID eventId;
    private String title;
    private String description;
    private String location;

    private String latitude;
    private String longitude;

    private BigDecimal amount;
    private Integer capacity;
    private Integer remainingCapacity;
    private String status;

    private LocalDateTime eventTimestamp;

    private String brochureUrl;
    private String qrCodeUrl;

    private Long totalRegistrations;
    private Long checkedInCount;

    public EventDetailsResponse() {
    }

    public EventDetailsResponse(UUID eventId, String title, String description, String location,
                                String latitude, String longitude, BigDecimal amount,
                                Integer capacity, Integer remainingCapacity, String status,
                                LocalDateTime eventTimestamp,
                                String brochureUrl, String qrCodeUrl,
                                Long totalRegistrations, Long checkedInCount) {

        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amount = amount;
        this.capacity = capacity;
        this.remainingCapacity = remainingCapacity;
        this.status = status;
        this.eventTimestamp = eventTimestamp;
        this.brochureUrl = brochureUrl;
        this.qrCodeUrl = qrCodeUrl;
        this.totalRegistrations = totalRegistrations;
        this.checkedInCount = checkedInCount;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(Integer remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getBrochureUrl() {
        return brochureUrl;
    }

    public void setBrochureUrl(String brochureUrl) {
        this.brochureUrl = brochureUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(Long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public Long getCheckedInCount() {
        return checkedInCount;
    }

    public void setCheckedInCount(Long checkedInCount) {
        this.checkedInCount = checkedInCount;
    }
}
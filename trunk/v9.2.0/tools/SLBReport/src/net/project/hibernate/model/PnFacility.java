package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnFacility implements Serializable {

    /** identifier field */
    private BigDecimal facilityId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String facilityType;

    /** nullable persistent field */
    private String roomName;

    /** nullable persistent field */
    private String roomNumber;

    /** nullable persistent field */
    private String building;

    /** nullable persistent field */
    private String floor;

    /** nullable persistent field */
    private String campus;

    /** nullable persistent field */
    private Integer isBridge;

    /** nullable persistent field */
    private String phoneNumber;

    /** nullable persistent field */
    private String password;

    /** nullable persistent field */
    private String facilityUrl;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnAddress pnAddress;

    /** persistent field */
    private Set pnCalendarEvents;

    /** full constructor */
    public PnFacility(BigDecimal facilityId, String name, String description, String facilityType, String roomName, String roomNumber, String building, String floor, String campus, Integer isBridge, String phoneNumber, String password, String facilityUrl, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnAddress pnAddress, Set pnCalendarEvents) {
        this.facilityId = facilityId;
        this.name = name;
        this.description = description;
        this.facilityType = facilityType;
        this.roomName = roomName;
        this.roomNumber = roomNumber;
        this.building = building;
        this.floor = floor;
        this.campus = campus;
        this.isBridge = isBridge;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.facilityUrl = facilityUrl;
        this.pnObject = pnObject;
        this.pnAddress = pnAddress;
        this.pnCalendarEvents = pnCalendarEvents;
    }

    /** default constructor */
    public PnFacility() {
    }

    /** minimal constructor */
    public PnFacility(BigDecimal facilityId, String name, String facilityType, net.project.hibernate.model.PnAddress pnAddress, Set pnCalendarEvents) {
        this.facilityId = facilityId;
        this.name = name;
        this.facilityType = facilityType;
        this.pnAddress = pnAddress;
        this.pnCalendarEvents = pnCalendarEvents;
    }

    public BigDecimal getFacilityId() {
        return this.facilityId;
    }

    public void setFacilityId(BigDecimal facilityId) {
        this.facilityId = facilityId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacilityType() {
        return this.facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuilding() {
        return this.building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCampus() {
        return this.campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Integer getIsBridge() {
        return this.isBridge;
    }

    public void setIsBridge(Integer isBridge) {
        this.isBridge = isBridge;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFacilityUrl() {
        return this.facilityUrl;
    }

    public void setFacilityUrl(String facilityUrl) {
        this.facilityUrl = facilityUrl;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnAddress getPnAddress() {
        return this.pnAddress;
    }

    public void setPnAddress(net.project.hibernate.model.PnAddress pnAddress) {
        this.pnAddress = pnAddress;
    }

    public Set getPnCalendarEvents() {
        return this.pnCalendarEvents;
    }

    public void setPnCalendarEvents(Set pnCalendarEvents) {
        this.pnCalendarEvents = pnCalendarEvents;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("facilityId", getFacilityId())
            .toString();
    }

}

package com.example.ioproject.models;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tachoid", unique = true, nullable = false)
    private Long tachoid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "device_id")
    private Long deviceId;

    public Driver() {
    }

    public Driver(Long tachoid, String firstName, String lastName, Long deviceId) {
        this.tachoid = tachoid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTachoid() {
        return tachoid;
    }

    public void setTachoid(Long tachoid) {
        this.tachoid = tachoid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
}

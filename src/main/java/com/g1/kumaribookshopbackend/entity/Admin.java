package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.AdminDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Admin extends SuperEntity<AdminDto> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String address;
    @Column
    private String userName;
    @Column
    private String password;


    @Override
    public AdminDto toDto() {
        AdminDto adminDto = new AdminDto();
        adminDto.setAdminId(this.adminId);
        adminDto.setFirstName(this.firstName);
        adminDto.setLastName(this.lastName);
        adminDto.setAddress(this.address);
        adminDto.setUserName(this.userName);
        adminDto.setPassword(this.password);
        return adminDto;
    }
}

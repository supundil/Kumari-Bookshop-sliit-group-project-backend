package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.AdminDto;
import com.g1.kumaribookshopbackend.enums.RecordStatus;
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
    private String name;
    @Column
    private String nic;
    @Column
    private String address;
    @Column
    private String userName;
    @Column
    private String password;
    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus = RecordStatus.ACTIVE;


    @Override
    public AdminDto toDto() {
        AdminDto adminDto = new AdminDto();
        adminDto.setCreatedDate(this.getCreatedDate());
        adminDto.setModifiedDate(this.getModifiedDate());
        adminDto.setAdminId(this.adminId);
        adminDto.setName(this.name);
        adminDto.setNic(this.nic);
        adminDto.setAddress(this.address);
        adminDto.setUserName(this.userName);
        return adminDto;
    }
}

package com.namjug.functionaltransaction.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class UserEntity {
    @Id
    private Long id;

    private String userName;
}

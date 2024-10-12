package com.luciano.springboot.ms.consumer.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {
    private String customerId;
    private String name;
    private String email;
}

package com.internship.payment_service.proxy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;

    private String name;

    private String surname;

    private String email;

}

package com.internship.payment_service.proxy;

import com.internship.authentication_library.feign.interceptor.UserServiceFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${microserviceUrls.user-service}",
        configuration = UserServiceFeignConfiguration.class)
public interface UserProxy {


    /**
     * Fetches a user from the user-service by the given id.
     *
     * @param id the id of the user to be fetched
     * @return a UserDTO containing the user's id
     */
    @GetMapping("/v1/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);


}

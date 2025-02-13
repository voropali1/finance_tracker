package cz.cvut.fel.pm2.UserMicroservice.feignclient;

import cz.cvut.fel.pm2.UserMicroservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", path = "/users")
public interface UserClient {
    @GetMapping("/info")
    ResponseEntity<UserDto> getUserInfo(@RequestHeader("Authorization") String token);

    @GetMapping("/{id}")
    ResponseEntity<UserDto> getUserById(@PathVariable("id") Integer id);
}
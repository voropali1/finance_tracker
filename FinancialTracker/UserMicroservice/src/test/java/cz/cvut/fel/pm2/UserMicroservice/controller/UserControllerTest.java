package cz.cvut.fel.pm2.UserMicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.pm2.UserMicroservice.config.TokenProvider;
import cz.cvut.fel.pm2.UserMicroservice.config.UnauthorizedEntryPoint;
import cz.cvut.fel.pm2.UserMicroservice.config.WebSecurityConfig;
import cz.cvut.fel.pm2.UserMicroservice.dto.UserDto;
import cz.cvut.fel.pm2.UserMicroservice.model.LoginUser;
import cz.cvut.fel.pm2.UserMicroservice.model.User;
import cz.cvut.fel.pm2.UserMicroservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(WebSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenProvider jwtTokenUtil;

    @MockBean(name = "userService")
    private UserService userService;

    @MockBean
    private UnauthorizedEntryPoint unauthorizedEntryPoint;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @Test
    public void testRegisterUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");

        User user = new User();
        user.setUsername("testuser");

        when(userService.save(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticate() throws Exception {
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("testuser");
        loginUser.setPassword("password");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtTokenUtil.generateToken(any())).thenReturn("testToken");

        mockMvc.perform(post("/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("testToken"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetUserInfo() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        when(userService.findOne("testuser")).thenReturn(user);

        mockMvc.perform(get("/users/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.id").value(1));
    }
}
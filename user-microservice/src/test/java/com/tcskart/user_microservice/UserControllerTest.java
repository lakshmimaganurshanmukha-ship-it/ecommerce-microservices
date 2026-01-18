package com.tcskart.user_microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
//import org.tcskart.user_microservice.controller.UserController;
import org.tcskart.user_microservice.controller.UserController;
import org.tcskart.user_microservice.dto.PasswordChange;
import org.tcskart.user_microservice.dto.UpdateUser;
import org.tcskart.user_microservice.entity.User;
import org.tcskart.user_microservice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)  // 👈 Disable filters unless testing security
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
	}

	@Test
	void testHelloEndpoint() throws Exception {
		mockMvc.perform(get("/users/hello"))
				.andExpect(status().isOk())
				.andExpect(content().string("hello"));
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	void testViewAllUsers() throws Exception {
		User user = new User();
		user.setUsername("testuser");
		when(userService.getAllUsers()).thenReturn(List.of(user));

		mockMvc.perform(get("/users/all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].userName").value("testuser"));
	}

	@Test
	void testUpdateUser() throws Exception {
		UpdateUser updateUser = new UpdateUser();
		updateUser.setName("john");

		mockMvc.perform(put("/users/update")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateUser)))
				.andExpect(status().isOk())
				.andExpect(content().string("User Updated"));

		verify(userService).updateUser(eq(updateUser), any(HttpServletRequest.class));
	}

	@Test
	void testChangePassword() throws Exception {
		PasswordChange passwordChange = new PasswordChange();
		passwordChange.setOldPassword("old123");
		passwordChange.setNewPassword("new123");

		mockMvc.perform(patch("/users/password")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(passwordChange)))
				.andExpect(status().isOk())
				.andExpect(content().string("Password Changed Sucessfully..."));

		verify(userService).changePassword(eq(passwordChange), any(HttpServletRequest.class));
	}

	@Test
	void testLogout_Success() throws Exception {
		when(userService.logOut(any(HttpServletRequest.class))).thenReturn(true);

		mockMvc.perform(get("/users/logout"))
				.andExpect(status().isOk())
				.andExpect(content().string("Succesfully logged out"));
	}

	@Test
	void testLogout_Failure() throws Exception {
		when(userService.logOut(any(HttpServletRequest.class))).thenReturn(false);

		mockMvc.perform(get("/users/logout"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Didn't able to log out"));
	}

	@Test
	@WithMockUser(roles = {"USER"})
	void testJwtEndpoint() throws Exception {
		mockMvc.perform(get("/users/jwt"))
				.andExpect(status().isOk())
				.andExpect(content().string("Jwt based login"));
	}

	@Test
	void testJwtWithoutAuth_ShouldFail() throws Exception {
		mockMvc.perform(get("/users/jwt"))
				.andExpect(status().isForbidden());
	}
}

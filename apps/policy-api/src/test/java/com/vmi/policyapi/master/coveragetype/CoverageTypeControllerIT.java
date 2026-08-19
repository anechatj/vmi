package com.vmi.policyapi.master.coveragetype;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmi.policyapi.AbstractIntegrationTest;

@AutoConfigureMockMvc
class CoverageTypeControllerIT extends AbstractIntegrationTest {

	private static final String BASE_URL = "/api/v1/master/coverage-types";

	@Autowired
	private MockMvc mockMvc;

	// สร้างเอง ไม่ @Autowired — Spring Boot 4 ย้ายไปใช้ Jackson 3 (tools.jackson) เป็นหลักแล้ว
	// ไม่มี bean ของ com.fasterxml.jackson.databind.ObjectMapper (Jackson 2) ให้ inject
	// อัตโนมัติอีกต่อไป แต่ยังใช้ serialize request body ธรรมดาสำหรับ test ได้เหมือนเดิม
	private final ObjectMapper objectMapper = new ObjectMapper();

	// mock authority ตรงๆ ผ่าน jwt() post processor — bypass JwtAuthenticationConverter จริง
	// ใน SecurityConfig ไปเลย เพราะ test นี้ต้องการตรวจสอบว่า @PreAuthorize บังคับสิทธิ์ถูก
	// ไม่ได้ต้องการ re-test ว่า realm_access.roles แปลงเป็น authority ถูก (จุดนั้น verify
	// แยกด้วย curl กับ token จริงจาก Keycloak แล้ว)
	private static RequestPostProcessor asAdmin() {
		return jwt().authorities(new SimpleGrantedAuthority("ROLE_admin"));
	}

	private static RequestPostProcessor asOfficer() {
		return jwt().authorities(new SimpleGrantedAuthority("ROLE_policy-officer"));
	}

	@Test
	void list_requiresAuthentication() throws Exception {
		mockMvc.perform(get(BASE_URL))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void create_forbidden_whenCallerLacksAdminRole() throws Exception {
		CoverageTypeCreateRequest request = new CoverageTypeCreateRequest("CLASS_3", "ชั้น 3", null, 4);

		mockMvc.perform(post(BASE_URL)
				.with(asOfficer())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden());
	}

	@Test
	void create_rejectsBlankName_withFieldErrors() throws Exception {
		CoverageTypeCreateRequest invalid = new CoverageTypeCreateRequest("CLASS_X", "", null, 1);

		mockMvc.perform(post(BASE_URL)
				.with(asAdmin())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalid)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fieldErrors[0].field", is("name")));
	}

	@Test
	void fullLifecycle_create_get_update_deactivate() throws Exception {
		CoverageTypeCreateRequest createRequest =
			new CoverageTypeCreateRequest("CLASS_1_IT", "ชั้น 1", "คุ้มครองสูงสุด", 1);

		String createResponse = mockMvc.perform(post(BASE_URL)
				.with(asAdmin())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code", is("CLASS_1_IT")))
			.andReturn().getResponse().getContentAsString();

		String id = objectMapper.readTree(createResponse).get("id").asText();

		// สร้างซ้ำด้วย code เดิมต้องโดน 409
		mockMvc.perform(post(BASE_URL)
				.with(asAdmin())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest)))
			.andExpect(status().isConflict());

		// user ทั่วไป (ไม่ใช่ admin) อ่านได้ปกติ
		mockMvc.perform(get(BASE_URL + "/{id}", id).with(asOfficer()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("ชั้น 1")))
			.andExpect(jsonPath("$.active", is(true)));

		CoverageTypeUpdateRequest updateRequest =
			new CoverageTypeUpdateRequest("ชั้น 1 (ปรับปรุง)", "คุ้มครองสูงสุด", 1);

		mockMvc.perform(put(BASE_URL + "/{id}", id)
				.with(asAdmin())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("ชั้น 1 (ปรับปรุง)")))
			.andExpect(jsonPath("$.code", is("CLASS_1_IT"))); // code ต้องไม่เปลี่ยน

		mockMvc.perform(delete(BASE_URL + "/{id}", id).with(asAdmin()))
			.andExpect(status().isNoContent());

		// soft delete — record ยังอยู่ แค่ active = false ไม่ใช่ 404
		mockMvc.perform(get(BASE_URL + "/{id}", id).with(asOfficer()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.active", is(false)));
	}

	@Test
	void get_returns404_whenIdDoesNotExist() throws Exception {
		mockMvc.perform(get(BASE_URL + "/{id}", "00000000-0000-0000-0000-000000000000").with(asOfficer()))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status", is(404)));
	}

}

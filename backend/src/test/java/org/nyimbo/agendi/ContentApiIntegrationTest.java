package org.nyimbo.agendi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {"NYIMBO_ADMIN_USERNAME=admin", "NYIMBO_ADMIN_PASSWORD=test-only-password"})
@AutoConfigureMockMvc
class ContentApiIntegrationTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("nyimbo").withUsername("nyimbo").withPassword("test-database-password");
    @Autowired MockMvc mvc;
    @DynamicPropertySource static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test void publicReadsExposeDemoChurchContentButNotHymns() throws Exception {
        mvc.perform(get("/api/v1/events").param("category", "Sabbath Worship"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].demo").value(true));
        mvc.perform(get("/api/v1/news")).andExpect(status().isOk()).andExpect(jsonPath("$[0].demo").value(true));
        mvc.perform(get("/api/v1/hymns").param("version", "0")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
    }

    @Test void adminRequiresRoleAndDraftContentStaysPrivate() throws Exception {
        mvc.perform(get("/api/v1/admin/events")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/admin/events").with(httpBasic("admin", "test-only-password")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").exists());
        String draft = """
            {"title":"Private draft","description":"For integration test","category":"Sabbath Worship","eventDate":"2027-01-02","startTime":"09:00:00","endTime":"11:00:00","venue":"Test venue","organizer":"Test organizer","published":false,"demo":false}
            """;
        mvc.perform(post("/api/v1/admin/events").with(httpBasic("admin", "test-only-password")).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(draft)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/events").param("q", "Private draft"))
            .andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
    }
}

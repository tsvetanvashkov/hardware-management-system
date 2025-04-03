package com.company.hardware_management_system;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.department.service.DepartmentService;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.office.repository.OfficeRepository;
import com.company.hardware_management_system.office.service.OfficeService;
import com.company.hardware_management_system.project.model.Project;
import com.company.hardware_management_system.user.model.User;
import com.company.hardware_management_system.web.dto.AddOfficeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfficeControllerTest.class)
@Import(OfficeControllerTest.TestConfig.class)
public class OfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private DepartmentService departmentService;

    static class TestConfig {
        @Bean
        DepartmentService projectService() {
            return mock(DepartmentService.class);
        }

        @Bean
        OfficeService projectRepository() {
            return mock(OfficeService.class);
        }

        @Bean
        OfficeRepository officeRepository() {
            return mock(OfficeRepository.class);
        }
    }

    // Test data builders
    private Office createTestOffice(UUID id, String name, String location) {
        return Office.builder()
                .id(id)
                .name(name)
                .location(location)
                .departments(new ArrayList<>())
                .users(new ArrayList<>())
                .hardware(new ArrayList<>())
                .build();
    }

    private Department createTestDepartment(UUID id, String name) {
        return Department.builder()
                .id(id)
                .name(name)
                .offices(new ArrayList<>())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOffices_shouldReturnViewWithOffices() throws Exception {
        // Arrange
        UUID officeId1 = UUID.randomUUID();
        UUID officeId2 = UUID.randomUUID();
        List<Office> offices = Arrays.asList(
                createTestOffice(officeId1, "HQ", "New York"),
                createTestOffice(officeId2, "Branch", "London")
        );

        when(officeService.getAllOffices()).thenReturn(offices);

        // Act & Assert
        mockMvc.perform(get("/offices"))
                .andExpect(status().isOk())
                .andExpect(view().name("offices"))
                .andExpect(model().attributeExists("offices"))
                .andExpect(model().attribute("offices", offices))
                .andExpect(xpath("//table/tbody/tr").nodeCount(2))
                .andExpect(xpath("//table/tbody/tr[1]/td[1]").string("HQ"))
                .andExpect(xpath("//table/tbody/tr[2]/td[1]").string("Branch"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAddOfficePage_shouldReturnFormWithEmptyRequest() throws Exception {
        mockMvc.perform(get("/offices/add-office"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-office"))
                .andExpect(model().attributeExists("addOfficeRequest"))
                .andExpect(xpath("//form[@class='form-container']").exists())
                .andExpect(xpath("//input[@th:field='*{name}']").exists())
                .andExpect(xpath("//textarea[@th:field='*{location}']").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addOffice_withValidRequest_shouldSaveAndRedirect() throws Exception {
        // Arrange
        AddOfficeRequest request = new AddOfficeRequest("Valid Name", "Valid Location");
        Office savedOffice = createTestOffice(UUID.randomUUID(), request.getName(), request.getLocation());

        when(officeService.addOffice(any(AddOfficeRequest.class))).thenReturn(savedOffice);

        // Act & Assert
        mockMvc.perform(post("/offices/add-office")
                        .with(csrf())
                        .param("name", request.getName())
                        .param("location", request.getLocation()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/offices"));

        verify(officeService).addOffice(any(AddOfficeRequest.class));
        verify(officeRepository).save(savedOffice);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addOffice_withBlankName_shouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post("/offices/add-office")
                        .with(csrf())
                        .param("name", "")  // Blank name
                        .param("location", "Valid Location"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-office"))
                .andExpect(model().attributeHasFieldErrors("addOfficeRequest", "name"))
                .andExpect(xpath("//p[@class='error' and contains(text(),'empty')]").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addOffice_withLongName_shouldReturnFormWithErrors() throws Exception {
        String longName = "This name is way too long and exceeds the 20 character limit";

        mockMvc.perform(post("/offices/add-office")
                        .with(csrf())
                        .param("name", longName)
                        .param("location", "Valid Location"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-office"))
                .andExpect(model().attributeHasFieldErrors("addOfficeRequest", "name"))
                .andExpect(xpath("//p[@class='error' and contains(text(),'20')]").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditOfficePage_shouldReturnFormWithOfficeData() throws Exception {
        // Arrange
        UUID officeId = UUID.randomUUID();
        UUID deptId1 = UUID.randomUUID();
        UUID deptId2 = UUID.randomUUID();


        List<Department> departments = List.of(
                Department.builder()
                        .id(deptId1)
                        .name("IT")
                        .build(),
                Department.builder()
                        .id(deptId2)
                        .name("HR")
                        .build()
        );

        Office office = Office.builder()
                .id(officeId)
                .name("Existing")
                .location("Location Desc")
                .departments(departments)
                .build();

        when(officeService.getOfficeById(officeId)).thenReturn(office);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act & Assert
        mockMvc.perform(get("/offices/{officeId}/edit", officeId))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-office"))
                .andExpect(model().attributeExists("office"))
                .andExpect(model().attributeExists("allDepartments"))
                .andExpect(model().attributeExists("editOfficeRequest"))
                .andExpect(xpath("//input[@name='name' and @value='Existing']").exists())
                .andExpect(xpath("//input[@name='departmentIds' and @checked]").nodeCount(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOffice_withDepartments_shouldUpdateRelationships() throws Exception {
        // Arrange
        UUID officeId = UUID.randomUUID();
        UUID deptId1 = UUID.randomUUID();
        UUID deptId2 = UUID.randomUUID();

        Office office = createTestOffice(officeId, "Old", "Old Location");
        Department dept1 = createTestDepartment(deptId1, "IT");
        Department dept2 = createTestDepartment(deptId2, "HR");

        when(officeService.getOfficeById(officeId)).thenReturn(office);
        when(departmentService.getDepartmentById(deptId1)).thenReturn(dept1);
        when(departmentService.getDepartmentById(deptId2)).thenReturn(dept2);

        // Act & Assert
        mockMvc.perform(post("/offices/{officeId}/edit", officeId)
                        .with(csrf())
                        .param("name", "Updated")
                        .param("location", "Updated Location")
                        .param("departmentIds", deptId1.toString(), deptId2.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/offices"));

        // Verify
        verify(office).setName("Updated");
        verify(office).setLocation("Updated Location");
        verify(office).setDepartments(argThat(departments ->
                departments.size() == 2 &&
                        departments.contains(dept1) &&
                        departments.contains(dept2)));
        verify(officeRepository).save(office);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOffice_withNoDepartments_shouldClearRelationships() throws Exception {
        // Arrange
        UUID officeId = UUID.randomUUID();
        Office office = createTestOffice(officeId, "Office", "Location");
        office.setDepartments(Arrays.asList(
                createTestDepartment(UUID.randomUUID(), "IT")
        ));

        // Create a valid AddOfficeRequest object
        AddOfficeRequest request = new AddOfficeRequest();
        request.setName("Updated");
        request.setLocation("Updated Location");

        when(officeService.getOfficeById(officeId)).thenReturn(office);
        when(officeRepository.save(any(Office.class))).thenReturn(office);

        // Act & Assert
        mockMvc.perform(post("/offices/{officeId}/edit", officeId)
                        .with(csrf())
                        .flashAttr("editOfficeRequest", request)  // Add the command object
                        .param("name", "Updated")
                        .param("location", "Updated Location"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/offices"));

        // Verify
        verify(office).setName("Updated");
        verify(office).setLocation("Updated Location");
        verify(office).setDepartments(Collections.emptyList());
        verify(officeRepository).save(office);
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoints_withUserRole_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/offices"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/offices/add-office"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/offices/" + UUID.randomUUID() + "/edit"))
                .andExpect(status().isForbidden());
    }
}

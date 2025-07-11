package com.sep.mmms_backend.repository;

import com.sep.mmms_backend.component.AuditorAwareImpl;
import com.sep.mmms_backend.config.JpaAuditingConfiguration;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@DataJpaTest(properties = {
    "spring.jpa.properties.jakarta.persistence.validation.mode=none",
})

@Import({JpaAuditingConfiguration.class, AuditorAwareImpl.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @MockitoBean
    AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * a valid meeting object
     */
    Meeting meeting;

    /**
     * a test user which is used by the auditor to populate the fields
     * the testUser's 'username' is configured in the Authentication object
     */
    AppUser testUser;

    //testUser does not have 'an' UID but savedTestUser does
    AppUser savedTestUser;

    @BeforeEach
    public void init() {
        //a valid meeting:
        meeting = Meeting.builder().title("Meeting").heldDate(LocalDate.now()).build();

        //mocking the SecurityContext as the AuditorAwareImpl uses this

        setUpSecurityContext("initialUsername");

    }

   public void setUpSecurityContext(String username) {

       testUser = AppUser.builder().username(username).firstName("firstName").lastName("lastName").password("password").email("email@gmail.com").build();

       SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
       List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
       UserDetails userDetails = org.springframework.security.core.userdetails.User
               .withUsername(testUser.getUsername())
               .password("") // Password can be empty as it's not used for this type of authentication
               .authorities(authorities)
               .accountExpired(false)
               .accountLocked(false)
               .credentialsExpired(false)
               .disabled(false)
               .build();

       UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
               userDetails,
               null,
               userDetails.getAuthorities()
       );

       securityContext.setAuthentication(authentication);
       SecurityContextHolder.setContext(securityContext);

       //mocked the appUserService which is used in AuditorAwareImpl
       Mockito.when(appUserService.loadUserByUsername(testUser.getUsername())).thenReturn(testUser);

       //saving the user because Meeting has OneToOne relationship with testUser with no any 'Cascade.PERSIST', so the AppUser has to be in the database
       savedTestUser = appUserRepository.save(testUser);
   }





    @Test
    public void saveMeeting_ShouldSetAuditingFields() {
        Meeting savedMeeting = meetingRepository.save(meeting);

        Meeting foundMeeting = meetingRepository.findById(savedMeeting.getId()).orElse(null);
        Assertions.assertThat(foundMeeting).isNotNull();

        //a) verify that the @CreatedBy and @UpdatedBy fields are set
        Assertions.assertThat(foundMeeting.getCreatedBy()).isNotNull();
        Assertions.assertThat(foundMeeting.getUpdatedBy()).isNotNull();
        Assertions.assertThat(foundMeeting.getCreatedBy()).isEqualTo(savedTestUser.getUsername());
        Assertions.assertThat(foundMeeting.getUpdatedBy()).isEqualTo(savedTestUser.getUsername());

        //b) Verify that the @CreatedDate and @LostModifiedDate fields are set

        Assertions.assertThat(foundMeeting.getCreatedDate()).isNotNull();
        Assertions.assertThat(foundMeeting.getUpdatedDate()).isNotNull();
        Assertions.assertThat(foundMeeting.getCreatedDate()).isEqualTo(LocalDate.now());
        Assertions.assertThat(foundMeeting.getUpdatedDate()).isEqualTo(LocalDate.now());

        log.info("Created By: {}", foundMeeting.getCreatedBy());
        log.info("Updated By: {}", foundMeeting.getUpdatedBy());
        log.info("Created Date: {}", foundMeeting.getCreatedDate());
        log.info("Updated Date: {}", foundMeeting.getUpdatedDate());
    }

    @Test
    public void updateMeeting_ShouldUpdateAuditingFields() {
       meetingRepository.save(meeting);

       Meeting initialSavedMeeting = meetingRepository.findById(meeting.getId()).orElse(null);
       Assertions.assertThat(initialSavedMeeting).isNotNull();


       //changing the SecurityContext>Authentication's username
       //this also changes the 'savedTestUser'
       setUpSecurityContext("updatedUsername");
       initialSavedMeeting.setTitle("updateMeeting");
       meetingRepository.save(initialSavedMeeting);

       //check if the Meeting is updated, not resaved
       Assertions.assertThat(meetingRepository.count()).isEqualTo(1);

       Meeting updatedMeeting = meetingRepository.findById(initialSavedMeeting.getId()).orElse(null);
       Assertions.assertThat(updatedMeeting).isNotNull();

       //CreatedBy and CreatedDate should not change
       Assertions.assertThat(updatedMeeting.getCreatedBy()).isEqualTo(initialSavedMeeting.getCreatedBy());
       Assertions.assertThat(updatedMeeting.getCreatedDate()).isEqualTo(initialSavedMeeting.getCreatedDate());

       //UpdateBy field should be updated
       Assertions.assertThat(updatedMeeting.getUpdatedBy()).isEqualTo(savedTestUser.getUsername());

       Assertions.assertThat(updatedMeeting.getUpdatedBy()).isEqualTo(savedTestUser.getUsername());

        log.info("CreatedBy: {}", updatedMeeting.getCreatedBy());
        log.info("Updated By field when the Meeting was created: {} ", updatedMeeting.getUpdatedBy());
        log.info("Updated By field when the Meeting was resaved by different user: {}", updatedMeeting.getUpdatedBy());
    }
}

package com.sep.mmms_backend.repository;


import com.sep.mmms_backend.component.AuditorAwareImpl;
import com.sep.mmms_backend.config.JpaAuditingConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Slf4j
@DataJpaTest(properties = {
        "spring.jpa.properties.jakarta.persistence.validation.mode=none",
})

@Import({JpaAuditingConfiguration.class, AuditorAwareImpl.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MemberRepositoryTests {

    //test the findByFirstNameOrLastName


    //test the findByFullName

    //testfindExistingMembersInCommittee

    //test findMemberById

    //test findMemberByIdNoException

    //test findAllMembersById

}

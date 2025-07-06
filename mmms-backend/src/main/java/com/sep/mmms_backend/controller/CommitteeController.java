package com.sep.mmms_backend.controller;


import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.CommitteeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
    }

    /**
     * {
     *     "committeeName": "New Committee",
     *     "committeeDescription": "Committee formed to handle stuffs",
     *     "memberships": [
     *          {
     *             "member": {
     *                  memberId:
     *             },
     *             "role": "SECRETARY",
     *          },
     *          {
     *              "member": {
     *                  memberId:
     *              },
     *              "role": "MEMBER",
     *          }
     *     ]
     * }
     *
     *HERE, the memberships object's only the member object's memberId is populated
     *
     * The committee field is left empty to be populated by the server itself.
     *
     * From the member id, the actual member object is fetched from the database and populated
     */
    @PostMapping("/createCommittee")
    public ResponseEntity<Response> createCommittee(@RequestBody Committee committee, Authentication authentication) {
        committeeService.createCommittee(committee, authentication.getName());
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEE_CREATION_SUCCESS));
    }
}

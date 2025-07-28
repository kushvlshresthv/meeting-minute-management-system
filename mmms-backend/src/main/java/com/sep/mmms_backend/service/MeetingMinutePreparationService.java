package com.sep.mmms_backend.service;
import org.apache.poi.xwpf.usermodel.*;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.MeetingDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MeetingMinutePreparationService {
    private final MeetingService meetingService;
    private final CommitteeRepository committeeRepository;
    private final MemberService memberService;
    private final TemplateEngine templateEngine;

    public MeetingMinutePreparationService(MeetingService meetingService, CommitteeRepository committeeRepository, MemberService memberService, TemplateEngine templateEngine) {
        this.meetingService = meetingService;
        this.memberService = memberService;
        this.committeeRepository = committeeRepository;
        this.templateEngine = templateEngine;
    }

    /**
     * Prepares all necessary data for the meeting minute view.
     *
     * @param committeeId The ID of the committee.
     * @param meetingId The ID of the meeting.
     * @param username The username of the authenticated  user .
     * @return A map containing all data attributes for the Thymeleaf model.
     * @throws CommitteeDoesNotExistException If the committee is not found in the database.
     * @throws MeetingDoesNotExistException If the mmeting is not found in the database.
     * @throws IllegalOperationException If the user does not have access to the committee/meeting.
     *
     */
    public Map<String, Object> prepareMeetingMinuteData(int committeeId, int meetingId, String username, String lang) {

        //1. validate committee is accessible by the user
        Committee committee = committeeRepository.findByIdNoException(committeeId).orElseThrow(CommitteeDoesNotExistException::new);

        if (!committee.getCreatedBy().getUsername().equals(username)) {
            throw new IllegalOperationException();
        }

        // 2. Validate Meeting and its association with Committee
        Meeting meeting = meetingService.findMeetingByIdNoException(meetingId)
                .orElseThrow(MeetingDoesNotExistException::new);

        if (!committee.getMeetings().contains(meeting)) {
            throw new IllegalOperationException();
        }

        // 3. Populate Data Map
        Map<String, Object> modelData = new HashMap<>();
//        modelData.put("meeting", meeting);
        modelData.put("meetingHeldDate", meeting.getHeldDate());
        modelData.put("meetingHeldDay", meeting.getHeldDate().getDayOfWeek().toString());
        modelData.put("partOfDay", getPartOfDay(meeting.getHeldTime(), lang));
        modelData.put("meetingHeldTime", meeting.getHeldTime());
        modelData.put("meetingHeldPlace", meeting.getHeldPlace());
        modelData.put("meetingTitle", meeting.getTitle());
        modelData.put("committeeName", meeting.getCommittee().getName());
        modelData.put("coordinatorFullName", formatCoordinatorFullName(meeting.getCoordinator(), lang));
        modelData.put("membershipsOfAttendees", getSortedAttendeesMemberships(meeting, committeeId, lang));
        modelData.put("decisions", meeting.getDecisions());

        return modelData;
    }

    private String getPartOfDay(LocalTime time, String lang) {
        int hour = time.getHour();
        String partOfDay;

        if (hour >= 5 && hour < 12) {
            partOfDay = lang.equalsIgnoreCase("nepali") ? "बिहान" : "Morning";
        } else if (hour >= 12 && hour < 17) {
            partOfDay = lang.equalsIgnoreCase("nepali") ? "दिउँसो" : "Afternoon";
        } else if (hour >= 17 && hour < 21) {
            partOfDay = lang.equalsIgnoreCase("nepali") ? "साँझ" : "Evening";
        } else {
            partOfDay = lang.equalsIgnoreCase("nepali") ? "राति" : "Night";
        }
        return partOfDay;
    }

    private String formatCoordinatorFullName(Member coordinator, String lang) {

        if(lang.equals("nepali")) return coordinator.getFirstNameNepali() + " " + coordinator.getLastNameNepali();

        else return coordinator.getFirstName() + " " + coordinator.getLastName();
    }

    private List<CommitteeMembership> getSortedAttendeesMemberships(Meeting meeting, int committeeId, String lang) {
        List<CommitteeMembership> membershipsOfAttendees = meeting.getAttendees().stream()
                .map(attendee -> {
                    CommitteeMembership membership = memberService.getMembership(attendee, committeeId);
                    //we are sure that attendee is part of the committee because we have already checked that meeting is part of the committee and a member can only be an attendee to a meeting if both belong to the same committee


                    //Furthermore, the coordinator is automatically moved to an attendee of the meeting when a member is registered as a coordinator for a meeting.
                    if (attendee.getId() == meeting.getCoordinator().getId()) {
                        membership.setRole("Coordinator");
                    }
                    return membership;
                })
                .collect(Collectors.toList());

        sortMembershipByRole(membershipsOfAttendees, lang);
        parsePostForTemplate(membershipsOfAttendees, lang);
        return membershipsOfAttendees;
    }

    void parsePostForTemplate(List<CommitteeMembership> membershipsOfAttendees, String lang) {
        for(CommitteeMembership membership: membershipsOfAttendees) {
            if(membership.getMember().getPost().equalsIgnoreCase("Doctor")){
                if(lang.equalsIgnoreCase("en")) membership.setRole("Dr");
                if(lang.equalsIgnoreCase("nepali")) membership.getMember().setPost("डा. ");
            }

            else if(membership.getMember().getPost().equalsIgnoreCase("Professor")){
                if(lang.equalsIgnoreCase("en")) membership.getMember().setPost("Prof.");
                if(lang.equalsIgnoreCase("nepali")) membership.getMember().setPost("प्रा.");
            }

            else {
                if(lang.equalsIgnoreCase("en")) membership.getMember().setPost("Mr. ");
                if(lang.equalsIgnoreCase("nepali")) membership.getMember().setPost("श्री");
            }

        }
    }

    /**
     * Sorts memberships object based on role in the order: 'coordinator -> member -> member_secretary -> invitee'
     */
    private void sortMembershipByRole(List<CommitteeMembership> memberships, String lang) {
        if (memberships == null || memberships.isEmpty()) {
            return;
        }
        Map<String, Integer> rolePriority = new HashMap<>();
        rolePriority.put("Coordinator", 1);
        rolePriority.put("Member", 2);
        rolePriority.put("Member_Secretary", 3);
        rolePriority.put("Invitee", 4);

        memberships.sort(Comparator.comparingInt(m -> rolePriority.getOrDefault(m.getRole(), Integer.MAX_VALUE)));

        if(lang.equals("nepali")) {
            for(CommitteeMembership membership : memberships) {
                if(membership.getRole().equalsIgnoreCase("coordinator")) {
                    membership.setRole("संयोजक");
                }

                else if(membership.getRole().equalsIgnoreCase("member")) {
                    membership.setRole("सदस्य");
                }

                else if(membership.getRole().equalsIgnoreCase("member-secretary")) {
                    membership.setRole("सदस्य-सचिव");
                }

                else if(membership.getRole().equalsIgnoreCase("invitee")) {
                    membership.setRole("आमन्त्रित");
                }
            }
        }
    }

    public String renderHtmlTemplate(String templateName, Map<String, Object> dataModel) {
        Context context = new Context();
        dataModel.forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }

    /*
    Possible classes that our templates can have:
    1. introduction -> signifies sections
    2. justify-text -> styling
    3. heading -> styling
    4. attendees -> signifies sections
    5. decisions -> signifies sections

    Structure of the template:

    #a4-box
        #introduction
            #introduction-body
        #attendees
            #heading-attendees
            #attendee-table
        #decisions
            #heading-decisions
            #decisions-list
    */

    public byte[] createWordDocumentFromHtml(String htmlContent) throws Exception {
        System.out.println(htmlContent);
        try (XWPFDocument document = new XWPFDocument()) {
            Document html = Jsoup.parse(htmlContent);

            XWPFParagraph paragraph = null;
            XWPFRun run = null;
            Element a4_box = html.getElementById("a4-box");
            if(a4_box == null) {
                throw new Exception();
            }



            for(Element element: a4_box.children()) {
                if(element.className().contains("introduction")) {

                    //rest of the classes(which are used for styling)
                    List<String> stylings = Arrays.asList(element.className().split("\\s+"));

                    Elements children = element.children();
                    for(Element child: children) {
                        if(child.className().equals("introduction-body")) {
                            paragraph = document.createParagraph();
                            paragraph.setSpacingAfter(100);
                            run = paragraph.createRun();
                            run.setText(element.text());

                            if(stylings.contains("justify-text")) {
                                styleJustifyText(paragraph);
                            }
                        }
                    }
                }

                else if (element.className().contains("attendees")) {
                    Elements children = element.children();

                    //attendee has two children, a heading, and the table
                    for(Element child: children) {

                        if(child.className().contains("heading")) {
                            paragraph = document.createParagraph();
                            paragraph.setSpacingBefore(100);
                            paragraph.setSpacingAfter(200);
                            styleHeading(paragraph.createRun(), child);
                        }

                        if(child.nodeName().equals("table")) {
                            XWPFTable newTable = document.createTable();
                            final int PADDING_LEFT = 100;
                            final int PADDING_TOP = 100;
                            newTable.setCellMargins(PADDING_TOP, PADDING_LEFT, 0,0 );
                            newTable.setWidth(XWPFTable.DEFAULT_PERCENTAGE_WIDTH);

                            copyTable(newTable, child);
                        }
                    }
                }

                else if(element.className().contains("decisions")) {
                    Elements children = element.children();

                    //deicisions has two children, a heading, and a list
                    for(Element child: children) {
                        if(child.className().contains("heading")) {
                            paragraph = document.createParagraph();
                            paragraph.setSpacingBefore(200);
                            paragraph.setSpacingAfter(200);
                            run = paragraph.createRun();
                            styleHeading(run, child);
                        }

                        if(child.nodeName().equals("ol")) {
                            Elements decisions = child.children();
                            int count = 1;
                            final int DECISION_SPACING = 17;

                            for(Element decision: decisions) {
                                paragraph = document.createParagraph();
                                paragraph.setSpacingBetween(DECISION_SPACING, LineSpacingRule.EXACT);
                                run = paragraph.createRun();
                                run.setText("  " +count + ".  " + decision.text());
                                count++;
                            }
                        }
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            byte[] bytes = out.toByteArray();
            return bytes;
        } catch(Exception e) {
            throw e;
        }
    }


    public void styleJustifyText(XWPFParagraph paragraph) {
        paragraph.setAlignment(ParagraphAlignment.BOTH);
    }

    public void styleHeading(XWPFRun run, Element element) {
        run.setText(element.text());
        run.setBold(true);
        run.setUnderline(UnderlinePatterns.SINGLE);
    }



    public void copyTable(XWPFTable newTable, Element oldTable) {
        //getting all the rows
        Elements oldRows = oldTable.select("tr");

        //iterate through the rows
        for(int i = 0; i<oldRows.size(); i++) {
            Element oldRow = oldRows.get(i);

            //getting the individual cells
            Elements oldCells = oldRow.select("th, td");

            //create new row(skip first because Apache POI creates one by default)
            XWPFTableRow newTableRow = (i==0) ? newTable.getRow(0): newTable.createRow();


            //set the min-height of the table row
            final int ROW_HEIGHT = 600;
            newTableRow.setHeight(ROW_HEIGHT);
            newTableRow.setHeightRule(TableRowHeightRule.AT_LEAST);

            //get the data from each cell and populate the XWPFTableRow
            for(int j = 0; j<oldCells.size(); j++) {
                String oldCellText = oldCells.get(j).text();
                //remove the first cell in the first row which is pre-built by the framework
                if(i==0 && j==0) {
                    newTableRow.removeCell(0);
                }

                XWPFTableCell cell = null;
                //only create new cells, if jth cell does not exist
                if(newTableRow.getTableCells().size()< j+1) {
                    cell = newTableRow.createCell();
                } else {
                    cell = newTableRow.getCell(j);
                }

                //remove the pre-built paragraph
                cell.removeParagraph(0);
                XWPFParagraph para = cell.addParagraph();
                XWPFRun run = para.createRun();
                run.setText(oldCellText);

                if(j==0) {
                    newTableRow.getCell(j).setWidth("5%");
                } else if(j==1 || j==2) {
                    newTableRow.getCell(j).setWidth("30%");
                } else if(j==3) {
                    newTableRow.getCell(j).setWidth("35%");
                }
            }
        }
    }
}



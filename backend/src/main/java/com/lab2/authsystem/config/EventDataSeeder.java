package com.lab2.authsystem.config;

import com.lab2.authsystem.model.Event;
import com.lab2.authsystem.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class EventDataSeeder {

    @Bean
    CommandLineRunner seedEvents(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() > 0) {
                return;
            }

            LocalDate baseDate = LocalDate.now().plusDays(7);

            List<Event> events = List.of(
                buildEvent(
                    "Engineering Innovation Expo 2026",
                    "Technology showcase for the College of Engineering and Architecture featuring prototypes, robotics demos, and student innovation exhibits.",
                    "technology",
                    baseDate.plusDays(0),
                    "09:00",
                    "15:00",
                    "CEA Design and Innovation Hall",
                    "College of Engineering and Architecture",
                    1L,
                    "College of Engineering and Architecture"
                ),
                buildEvent(
                    "Accountancy Career Fair 2026",
                    "Career development event for the College of Management, Business and Accountancy with recruiters, alumni speakers, and internship booths.",
                    "career",
                    baseDate.plusDays(3),
                    "10:00",
                    "16:00",
                    "CMBA Function Room",
                    "College of Management, Business and Accountancy",
                    2L,
                    "College of Management, Business and Accountancy"
                ),
                buildEvent(
                    "Research Seminar on Science Education",
                    "Academic seminar for the College of Arts, Science, and Education covering classroom research, teaching practice, and interdisciplinary studies.",
                    "academic",
                    baseDate.plusDays(6),
                    "13:00",
                    "16:30",
                    "CASE Lecture Auditorium",
                    "College of Arts, Science, and Education",
                    3L,
                    "College of Arts, Science, and Education"
                ),
                buildEvent(
                    "Nursing Skills Workshop and Health Forum",
                    "Academic and professional workshop for the College of Nursing and Allied Health Sciences focused on patient care simulations and community health updates.",
                    "academic",
                    baseDate.plusDays(9),
                    "08:30",
                    "12:00",
                    "CNAHS Skills Laboratory",
                    "College of Nursing and Allied Health Sciences",
                    4L,
                    "College of Nursing and Allied Health Sciences"
                ),
                buildEvent(
                    "Hackathon and Code Challenge 2026",
                    "Technology competition for the College of Computer Studies featuring coding rounds, systems design, and innovation pitching.",
                    "technology",
                    baseDate.plusDays(12),
                    "09:00",
                    "18:00",
                    "CCS Innovation Hub",
                    "College of Computer Studies",
                    5L,
                    "College of Computer Studies"
                ),
                buildEvent(
                    "Criminal Justice Leadership Forum",
                    "Academic forum for the College of Criminal Justice on ethics, leadership, and public safety practice.",
                    "academic",
                    baseDate.plusDays(15),
                    "14:00",
                    "17:00",
                    "CCJ Moot Court Hall",
                    "College of Criminal Justice",
                    6L,
                    "College of Criminal Justice"
                ),
                buildEvent(
                    "Buwan ng Wika Cultural Festival",
                    "Cultural event with performances, exhibits, and student participation from multiple colleges, led by the College of Arts, Science, and Education.",
                    "cultural",
                    baseDate.plusDays(18),
                    "15:00",
                    "20:00",
                    "University Open Grounds",
                    "College of Arts, Science, and Education",
                    3L,
                    "College of Arts, Science, and Education"
                ),
                buildEvent(
                    "University Sports Tournament Opening",
                    "Sports event featuring inter-college participation and opening ceremonies for the campus athletic season.",
                    "sports",
                    baseDate.plusDays(21),
                    "07:30",
                    "17:00",
                    "University Gymnasium",
                    "All Colleges",
                    7L,
                    "Student Affairs and Sports Office"
                ),
                buildEvent(
                    "Campus Social and Acquaintance Party",
                    "Social event for students across all colleges to meet organizations, peers, and campus services.",
                    "social",
                    baseDate.plusDays(24),
                    "17:00",
                    "21:00",
                    "Student Center Plaza",
                    "All Colleges",
                    8L,
                    "Student Affairs Office"
                )
            );

            eventRepository.saveAll(events);
        };
    }

    private Event buildEvent(
        String title,
        String description,
        String category,
        LocalDate date,
        String startTime,
        String endTime,
        String location,
        String department,
        Long organizerId,
        String organizerName
    ) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setCategory(category);
        event.setDate(date);
        event.setTime(LocalTime.parse(startTime));
        event.setEndTime(LocalTime.parse(endTime));
        event.setLocation(location);
        event.setDepartment(department);
        event.setOrganizerId(organizerId);
        event.setOrganizerName(organizerName);
        return event;
    }
}

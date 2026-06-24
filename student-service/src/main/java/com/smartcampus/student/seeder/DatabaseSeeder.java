package com.smartcampus.student.seeder;

import com.smartcampus.student.entity.Student;
import com.smartcampus.student.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final StudentRepository repository;

    public DatabaseSeeder(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        // If database is empty, seed with the five requested students
        if (repository.count() == 0) {
            System.out.println("[DatabaseSeeder] Seeding database with the five custom students...");
            
            List<Student> students = Arrays.asList(
                    new Student("S1001", "Syahmi Dhabit", "syahmi.dhabit@smartcampus.edu", "Computer Science", 3.82),
                    new Student("S1002", "Aina Maisarah", "aina.maisarah@smartcampus.edu", "Information Systems", 3.91),
                    new Student("S1003", "Muhammad Syahmi", "muhammad.syahmi@smartcampus.edu", "Software Engineering", 3.75),
                    new Student("S1004", "Abdul Aziz", "abdul.aziz@smartcampus.edu", "Computer Science", 3.95),
                    new Student("S1005", "Fiona", "fiona@smartcampus.edu", "Data Science", 3.60)
            );
            
            repository.saveAll(students);
            System.out.println("[DatabaseSeeder] Seeded " + repository.count() + " students successfully.");
        }
    }
}

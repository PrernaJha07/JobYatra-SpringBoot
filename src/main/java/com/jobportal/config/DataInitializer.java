package com.jobportal.config;

import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeSampleJobs();
        initializeSampleUser();
    }

    private void initializeAdminUser() {
        if (!userRepository.findByEmail(adminEmail).isPresent()) {
            User adminUser = new User();
            adminUser.setFullName("Admin User");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(adminPassword);
            adminUser.setAddress("Admin Headquarters, Silicon Valley");
            adminUser.setPhone("+1-555-0001");
            adminUser.setAdmin(true);
            userRepository.save(adminUser);
            System.out.println("✅ Admin user created: " + adminEmail);
        }
    }

    private void initializeSampleUser() {
        if (!userRepository.findByEmail("user@example.com").isPresent()) {
            User sampleUser = new User();
            sampleUser.setFullName("John Doe");
            sampleUser.setEmail("user@example.com");
            sampleUser.setPassword("password123");
            sampleUser.setAddress("123 Main Street, New York, NY");
            sampleUser.setPhone("+1-555-0123");
            sampleUser.setAdmin(false);
            userRepository.save(sampleUser);
            System.out.println("✅ Sample user created: user@example.com");
        }
    }

    private void initializeSampleJobs() {
        if (jobRepository.count() == 0) {
            // Job 1 - Google
            Job job1 = new Job();
            job1.setTitle("Senior Software Engineer");
            job1.setDescription("Develop and maintain scalable web applications using modern technologies like Java, Spring Boot, and cloud platforms. Work with cross-functional teams to deliver high-quality software solutions.");
            job1.setCompany("Google");
            job1.setLocation("Mountain View, CA");
            job1.setSalary("$150,000 - $200,000");
            job1.setJobType("Full-time");
            job1.setCompanyLogoPath("/uploads/google_logo.png");
            job1.setActive(true);

            // Job 2 - Microsoft
            Job job2 = new Job();
            job2.setTitle("Frontend Developer");
            job2.setDescription("Create responsive and interactive user interfaces using React, Angular, and Vue.js. Collaborate with UX designers to implement beautiful user experiences for enterprise applications.");
            job2.setCompany("Microsoft");
            job2.setLocation("Seattle, WA");
            job2.setSalary("$120,000 - $160,000");
            job2.setJobType("Full-time");
            job2.setCompanyLogoPath("/uploads/microsoft_logo.png");
            job2.setActive(true);

            // Job 3 - Amazon
            Job job3 = new Job();
            job3.setTitle("Data Scientist");
            job3.setDescription("Analyze complex datasets and build machine learning models using Python, R, and AWS services. Work with big data technologies to derive actionable insights for business decisions.");
            job3.setCompany("Amazon");
            job3.setLocation("New York, NY");
            job3.setSalary("$140,000 - $180,000");
            job3.setJobType("Full-time");
            job3.setCompanyLogoPath("/uploads/amazon_logo.png");
            job3.setActive(true);

            // Job 4 - Apple
            Job job4 = new Job();
            job4.setTitle("Product Manager");
            job4.setDescription("Lead product development from conception to launch. Define product strategy, gather requirements, and work with engineering teams to deliver exceptional products for iOS and macOS platforms.");
            job4.setCompany("Apple");
            job4.setLocation("Cupertino, CA");
            job4.setSalary("$160,000 - $220,000");
            job4.setJobType("Full-time");
            job4.setCompanyLogoPath("/uploads/apple_logo.png");
            job4.setActive(true);

            // Job 5 - Netflix
            Job job5 = new Job();
            job5.setTitle("DevOps Engineer");
            job5.setDescription("Manage cloud infrastructure and deployment pipelines on AWS. Implement CI/CD processes using Jenkins, Docker, and Kubernetes to ensure system reliability and scalability.");
            job5.setCompany("Netflix");
            job5.setLocation("Remote");
            job5.setSalary("$130,000 - $170,000");
            job5.setJobType("Remote");
            job5.setCompanyLogoPath("/uploads/netflix_logo.png");
            job5.setActive(true);

            // Job 6 - Meta
            Job job6 = new Job();
            job6.setTitle("UX Designer");
            job6.setDescription("Design user-centered interfaces and experiences for social media platforms. Conduct user research and create wireframes and prototypes for web and mobile applications.");
            job6.setCompany("Meta");
            job6.setLocation("Menlo Park, CA");
            job6.setSalary("$110,000 - $150,000");
            job6.setJobType("Full-time");
            job6.setCompanyLogoPath("/uploads/meta_logo.png");
            job6.setActive(true);

            // Job 7 - Tesla
            Job job7 = new Job();
            job7.setTitle("Full Stack Developer");
            job7.setDescription("Build and maintain web applications using modern JavaScript frameworks. Work on both frontend and backend development for automotive technology platforms.");
            job7.setCompany("Tesla");
            job7.setLocation("Austin, TX");
            job7.setSalary("$135,000 - $175,000");
            job7.setJobType("Full-time");
            job7.setCompanyLogoPath("/uploads/tesla_logo.png");
            job7.setActive(true);

            // Job 8 - Spotify
            Job job8 = new Job();
            job8.setTitle("Backend Engineer");
            job8.setDescription("Develop scalable backend services for music streaming platform using Java and microservices architecture. Work on audio processing and recommendation systems.");
            job8.setCompany("Spotify");
            job8.setLocation("New York, NY");
            job8.setSalary("$125,000 - $165,000");
            job8.setJobType("Full-time");
            job8.setCompanyLogoPath("/uploads/spotify_logo.png");
            job8.setActive(true);

            jobRepository.saveAll(Arrays.asList(job1, job2, job3, job4, job5, job6, job7, job8));
            System.out.println("✅ Sample jobs initialized: " + jobRepository.count() + " jobs created with proper company names");
        } else {
            System.out.println("✅ Jobs already exist in database: " + jobRepository.count() + " jobs found");
        }
    }
}
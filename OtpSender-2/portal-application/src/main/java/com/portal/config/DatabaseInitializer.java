package com.portal.config;

import com.portal.entity.Application;
import com.portal.entity.Role;
import com.portal.entity.User;
import com.portal.repository.ApplicationRepository;
import com.portal.repository.RoleRepository;
import com.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Initialize Roles if empty
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_HR"));
            roleRepository.save(new Role("ROLE_FINANCE"));
            roleRepository.save(new Role("ROLE_IT"));
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElse(null);
        Role hrRole = roleRepository.findByName("ROLE_HR").orElse(null);
        Role financeRole = roleRepository.findByName("ROLE_FINANCE").orElse(null);
        Role itRole = roleRepository.findByName("ROLE_IT").orElse(null);

        // Initialize Users if empty
        if (userRepository.count() == 0) {
            // Create Admin
            User admin = new User(
                "admin",
                passwordEncoder.encode("admin"),
                "admin@company.com",
                "Information Technology",
                "Administrator"
            );
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole, itRole)));
            userRepository.save(admin);

            // Create Standard User
            User standardUser = new User(
                "user",
                passwordEncoder.encode("user"),
                "user@company.com",
                "Finance Department",
                "John Doe"
            );
            standardUser.setRoles(new HashSet<>(Arrays.asList(hrRole, financeRole)));
            userRepository.save(standardUser);
        }

        // Initialize Applications if empty
        if (applicationRepository.count() == 0) {
            // 1. HR Portal
            Application hrPortal = new Application(
                "HR Management",
                "Access leave requests, payslips, benefits tracker, and employee appraisals.",
                "https://example.com/hr-portal",
                "users",
                "HR",
                "linear-gradient(135deg, rgba(79, 70, 229, 0.4), rgba(129, 140, 248, 0.4))"
            );
            hrPortal.getRoles().addAll(Arrays.asList(adminRole, hrRole));
            applicationRepository.save(hrPortal);

            // 2. Expense Tracker
            Application expenseTracker = new Application(
                "Expense Tracker",
                "Submit corporate claims, track budgets, upload receipts, and check reimbursement status.",
                "https://example.com/expense-tracker",
                "dollar-sign",
                "Finance",
                "linear-gradient(135deg, rgba(5, 150, 105, 0.4), rgba(52, 211, 153, 0.4))"
            );
            expenseTracker.getRoles().addAll(Arrays.asList(adminRole, financeRole));
            applicationRepository.save(expenseTracker);

            // 3. IT support
            Application supportDesk = new Application(
                "IT Support Desk",
                "Raise hardware or software support tickets, view system statuses, and browse FAQs.",
                "https://example.com/support-desk",
                "shield",
                "IT",
                "linear-gradient(135deg, rgba(217, 119, 6, 0.4), rgba(251, 191, 36, 0.4))"
            );
            supportDesk.getRoles().addAll(Arrays.asList(adminRole, itRole));
            applicationRepository.save(supportDesk);

            // 4. DB Monitor (Admin only)
            Application dbMonitor = new Application(
                "Database Monitor",
                "Monitor query executions, verify database engine health metrics, and view connection pools.",
                "https://example.com/db-monitor",
                "database",
                "IT",
                "linear-gradient(135deg, rgba(220, 38, 38, 0.4), rgba(248, 113, 113, 0.4))"
            );
            dbMonitor.getRoles().add(adminRole);
            applicationRepository.save(dbMonitor);

            // 5. Corporate Directory (All users)
            Application directory = new Application(
                "Corporate Directory",
                "Lookup contact details, email addresses, and organizational hierarchies for teams.",
                "https://example.com/directory",
                "book-open",
                "Management",
                "linear-gradient(135deg, rgba(37, 99, 235, 0.4), rgba(96, 165, 250, 0.4))"
            );
            directory.getRoles().addAll(Arrays.asList(adminRole, hrRole, financeRole, itRole));
            applicationRepository.save(directory);
        }
    }
}

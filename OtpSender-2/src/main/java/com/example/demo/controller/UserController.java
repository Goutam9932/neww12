package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.User;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private OtpService otpService;
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, 
                               @RequestParam("imageFile") MultipartFile imageFile,
                               RedirectAttributes redirectAttributes) {
        try {
            // Save the user
            userService.saveUser(user, imageFile);

            // Send registration email with correct username and password
            otpService.sendRegistrationEmail(user.getEmail(), user.getUsername(), user.getPassword());

            // Display success message on UI
            redirectAttributes.addFlashAttribute("successMessage", "User registered successfully! Check your email for login details.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image.");
        }
        return "redirect:/register";
    }

    @GetMapping("/admindetails")
    public String getAdminDetails(Model model) {
        model.addAttribute("admins", userService.getAdminDetails());
        return "admindetails";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam("name") String name, Model model) {
        List<User> users = userService.searchUsersByName(name);
        if (!users.isEmpty()) {
            User user = users.get(0);
            if ("admin".equalsIgnoreCase(user.getRole())) {
                model.addAttribute("admins", List.of(user));
                return "singleuser";
            } else {
                model.addAttribute("user", user);
                return "singleuser";
            }
        } else {
            model.addAttribute("error", "No user found with the name: " + name);
            return "userdetails";
        }
    }

    @GetMapping("/admin/edit")
    public String showEditForm1(@RequestParam("id") Long id, Model model) {
        User user = userService.findUserById(id);
        if (user != null) {
            model.addAttribute("admin", user); // Change 'user' to 'admin' for consistency
            return "admin-edit"; // Ensure this matches your Thymeleaf template name
        } else {
            // Handle case where user is not found (e.g., return an error page or redirect)
            return "redirect:/admindetails"; // Or another appropriate action
        }
    }


    @PostMapping("/admin/update")
    public String saveAdmin(@ModelAttribute User admin, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            userService.updateAdmin(admin, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            // Consider adding an error message here to inform the user
        }
        return "redirect:/userdetails";
    }


    @GetMapping("/user/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findUserById(id);
        
        // Check if user is found
        if (user != null) {
            // Add user to model for the edit form
            model.addAttribute("user", user);
            return "user-edit"; // Ensure this view exists for editing users
        } else {
            // Handle case where user is not found
            model.addAttribute("errorMessage", "User not found.");
            return "error"; // Redirect to an error page or handle as needed
        }
    }


    @PostMapping("/user/edit")
    public String editUser(@ModelAttribute User user,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           Model model) {
        try {
            userService.updateUser(user, imageFile);
            model.addAttribute("user", user); // To display updated info on the UI if needed
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error uploading image");
            return "user-edit"; // Return to edit page in case of an error
        }
        return "redirect:/user/dashboard"; // Redirect to dashboard or another page after saving
    }

    @GetMapping("/userdetails")
    public String listUsers(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 4); // Change the size as needed
        Page<User> usersPage = userService.findAllUsers(pageable);
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        return "userDetails"; // Return your Thymeleaf template name
    }
    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/userdetails";
    }
    
}

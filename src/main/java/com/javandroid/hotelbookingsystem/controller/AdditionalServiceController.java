package com.javandroid.hotelbookingsystem.controller;

import com.javandroid.hotelbookingsystem.model.AdditionalService;
import com.javandroid.hotelbookingsystem.service.AdditionalServiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/additional-services")
public class AdditionalServiceController {
    private final AdditionalServiceService additionalServiceService;

    public AdditionalServiceController(AdditionalServiceService additionalServiceService) {
        this.additionalServiceService = additionalServiceService;
    }

    // ✅ Display all additional services
    @GetMapping
    public String getAllServices(Model model) {
        List<AdditionalService> services = additionalServiceService.getAllServices();
        model.addAttribute("services", services);
        return "additional-services"; // Load additional-services.html
    }

    // ✅ Show form to add a service to a booking
    @GetMapping("/add")
    public String showServiceForm(Model model) {
        model.addAttribute("service", new AdditionalService());
        return "service-form"; // Load service-form.html
    }

    // ✅ Handle adding service to a booking
    @PostMapping("/add")
    public String addServiceToBooking(@RequestParam int bookingId, @RequestParam int serviceId) {
        additionalServiceService.addServiceToBooking(bookingId, serviceId);
        return "redirect:/additional-services"; // Refresh services list
    }
}

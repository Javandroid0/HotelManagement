package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.model.AdditionalService;
import com.javandroid.hotelbookingsystem.repository.AdditionalServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdditionalServiceService {
    private final AdditionalServiceRepository additionalServiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdditionalServiceService.class);

    public AdditionalServiceService(AdditionalServiceRepository additionalServiceRepository) {
        this.additionalServiceRepository = additionalServiceRepository;
    }

    // ✅ Get all additional services
    public List<AdditionalService> getAllServices() {
        logger.info("Fetching all additional services...");
        return additionalServiceRepository.getAllServices();
    }

    // ✅ Add service to a booking
    public void addServiceToBooking(int bookingId, int serviceId) {
        logger.info("Adding service ID {} to Booking ID {}", serviceId, bookingId);
        additionalServiceRepository.addServiceToBooking(bookingId, serviceId);
    }
}

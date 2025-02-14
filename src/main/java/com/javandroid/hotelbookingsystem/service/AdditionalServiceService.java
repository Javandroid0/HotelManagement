package com.javandroid.hotelbookingsystem.service;

import com.javandroid.hotelbookingsystem.exception.ResourceNotFoundException;
import com.javandroid.hotelbookingsystem.model.AdditionalService;
import com.javandroid.hotelbookingsystem.repository.AdditionalServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdditionalServiceService {
    private final AdditionalServiceRepository additionalServiceRepository;
    private static final Logger logger = LoggerFactory.getLogger(AdditionalServiceService.class);

    public AdditionalServiceService(AdditionalServiceRepository additionalServiceRepository) {
        this.additionalServiceRepository = additionalServiceRepository;
    }

    //Retrieve all available services
    public List<AdditionalService> getAllServices() {
        logger.info("Fetching all available additional services...");
        return additionalServiceRepository.getAllServices();
    }

    //Get service by ID
    public AdditionalService getServiceById(int id) {
        logger.info("Fetching service with ID {}", id);
        return additionalServiceRepository.getServiceById(id)
                .orElseThrow(() -> {
                    logger.error("Service with ID {} not found", id);
                    return new ResourceNotFoundException("Service with ID " + id + " not found.");
                });
    }

    //Request an additional service for a booking
    @Transactional
    public void addServiceToBooking(int bookingId, int serviceId) {
        logger.info("Adding Service ID {} to Booking ID {}", serviceId, bookingId);
        additionalServiceRepository.addServiceToBooking(bookingId, serviceId);
        logger.info("Service ID {} successfully added to Booking ID {}", serviceId, bookingId);
    }

    //Retrieve all services linked to a specific booking
    public List<AdditionalService> getServicesForBooking(int bookingId) {
        logger.info("Fetching services for Booking ID {}", bookingId);
        return additionalServiceRepository.getServicesForBooking(bookingId);
    }

    //Cancel a requested service from a booking
    @Transactional
    public void removeServiceFromBooking(int bookingId, int serviceId) {
        logger.info("Removing Service ID {} from Booking ID {}", serviceId, bookingId);
        additionalServiceRepository.removeServiceFromBooking(bookingId, serviceId);
        logger.info("Service ID {} successfully removed from Booking ID {}", serviceId, bookingId);
    }
}

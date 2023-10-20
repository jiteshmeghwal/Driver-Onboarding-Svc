package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/onboarding-steps")
public class OnboardingStepController {
    @Autowired
    private OnboardingStepService onboardingStepService;

    @GetMapping("/{driverId}")
    @Secured("ROLE_ADMIN")
    public List<OnboardingStepDTO> getOnboardingStepsByDriver(@PathVariable("driverId") String driverId) {
        return onboardingStepService.getOnboardingStepsByDriver(driverId);
    }

    @GetMapping("/next-incomplete")
    public ResponseEntity<?> getNextIncompleteStep() {
        Optional<OnboardingStepDTO> onboardingStep = onboardingStepService.getNextIncompleteStep(
                RequestContextStore.getUser().getUsername());
        if(onboardingStep.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(onboardingStep.get(), HttpStatus.OK);
    }

    @PutMapping("/mark-incomplete")
    @Secured("ROLE_ADMIN")
    public void markStepIncomplete(@RequestParam("driverId") String driverId,
                                   @RequestParam("stepId") Short stepId)
            throws ValidationException {
        onboardingStepService.updateCompleteStatus(stepId, driverId, false);
    }
}

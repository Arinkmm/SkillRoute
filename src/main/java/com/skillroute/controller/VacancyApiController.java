package com.skillroute.controller;

import com.skillroute.api.generated.api.VacanciesApi;
import com.skillroute.api.generated.dto.CreateVacancyRequest;
import com.skillroute.api.generated.dto.UpdateVacancyRequest;
import com.skillroute.api.generated.dto.VacancyResponse;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VacancyApiController implements VacanciesApi {

    private final VacancyService vacancyService;

    @Override
    public ResponseEntity<List<VacancyResponse>> companyVacanciesGet() {
        CustomUserDetails user = getCurrentUser();

        return ResponseEntity.ok(
                vacancyService.getVacanciesByCompany(user.getId())
        );
    }

    @Override
    public ResponseEntity<VacancyResponse> companyVacanciesPost(CreateVacancyRequest createVacancyRequest) {
        CustomUserDetails user = getCurrentUser();

        VacancyResponse vacancy = vacancyService.createVacancy(
                createVacancyRequest,
                user.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(vacancy);
    }

    @Override
    public ResponseEntity<VacancyResponse> companyVacanciesIdGet(Long id) {
        CustomUserDetails user = getCurrentUser();

        return ResponseEntity.ok(
                vacancyService.getVacancyById(id, user.getId())
        );
    }

    @Override
    public ResponseEntity<Void> companyVacanciesIdPatch(Long id, UpdateVacancyRequest updateVacancyRequest) {
        CustomUserDetails user = getCurrentUser();

        vacancyService.updateVacancy(
                id,
                updateVacancyRequest,
                user.getId()
        );

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> companyVacanciesIdDelete(Long id) {
        CustomUserDetails user = getCurrentUser();

        vacancyService.deleteVacancy(id, user.getId());

        return ResponseEntity.noContent().build();
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный authentication principal");
        }

        return user;
    }
}
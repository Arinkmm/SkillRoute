package com.skillroute.controller;

import com.skillroute.dto.response.ErrorResponse;
import com.skillroute.dto.response.StudentSkillResponse;
import com.skillroute.service.StudentSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/skills")
@RequiredArgsConstructor
@Tag(name = "Поиск навыков", description = "Операции по поиску и фильтрации навыков в словаре системы")
public class StudentSkillRestController {
    private final StudentSkillService studentSkillService;

    @Operation(
            summary = "Поиск навыков по подстроке",
            description = "Возвращает список навыков студента, чьи названия содержат указанный текст. Поиск регистронезависимый"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список навыков успешно получен",
                    content = @Content(
                            schema = @Schema(implementation = StudentSkillResponse.class),
                            examples = @ExampleObject(value = "[{\"skillId\": 1, \"name\": \"Java\", \"level\": 4, \"isConfirmedByGitHub\": true}]")
                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Произошла непредвиденная ошибка при поиске\", \"errorCode\": 500}")
                    ))
    })
    @GetMapping("/search")
    public ResponseEntity<List<StudentSkillResponse>> searchSkills(
            @Parameter(description = "Название или часть названия навыка", example = "Java")
            @RequestParam("name") String name) {
        return ResponseEntity.ok(studentSkillService.getStudentsSkillsByName(name));
    }
}
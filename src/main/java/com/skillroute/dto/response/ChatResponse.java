package com.skillroute.dto.response;

import com.skillroute.model.StudentVacancyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные чата с сообщениями")
public class ChatResponse {
    @Schema(description = "Уникальный идентификатор чата", example = "12")
    private Long chatId;

    @Schema(description = "Имя собеседника текущего пользователя", example = "ООО Ромашка")
    private String opponentName;

    @Schema(description = "Сообщения чата в порядке отправки")
    private List<MessageResponse> messages;

    @Schema(description = "Признак того, что чат открыт компанией", example = "true")
    private boolean companyView;

    @Schema(description = "ID студента в активном отклике", example = "5")
    private Long studentId;

    @Schema(description = "ID вакансии в активном отклике", example = "17")
    private Long vacancyId;

    @Schema(description = "Название вакансии в активном отклике", example = "Java Backend Intern")
    private String vacancyName;

    @Schema(description = "Статус студента по активной вакансии", example = "INTERVIEW")
    private StudentVacancyStatus studentVacancyStatus;

    @Schema(description = "Признак завершенного диалога", example = "false")
    private boolean closed;
}

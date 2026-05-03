package com.skillroute.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    @NotBlank(message = "Текст сообщения не может быть пустым")
    @Size(max = 500, message = "Сообщение не должно превышать 500 символов")
    private String text;
}
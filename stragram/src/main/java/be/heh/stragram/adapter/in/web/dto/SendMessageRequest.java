package be.heh.stragram.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    
    @NotNull(message = "Receiver ID is required")
    private UUID receiverId;
    
    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 1000, message = "Message content cannot exceed 1000 characters")
    private String content;
}

package org.example.edusoft.learning.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingListRequest {
    private Long practiceId;
    private Long classId;
}

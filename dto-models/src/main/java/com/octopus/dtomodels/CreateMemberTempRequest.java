package com.octopus.dtomodels;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMemberTempRequest {
    private String email;
}

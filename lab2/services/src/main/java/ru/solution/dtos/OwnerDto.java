package ru.solution.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private List<CatDto> cats;
}

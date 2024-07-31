package ru.solution.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatDto {
    private long id;
    private String name;
    private LocalDate birthDate;
    private String breed;
    private String color;
    private OwnerDto owner;
    private List<CatDto> friends;
}

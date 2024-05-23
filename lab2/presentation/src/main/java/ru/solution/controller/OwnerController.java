package ru.solution.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.solution.OwnerService.OwnerService;
import ru.solution.dtos.OwnerDto;
import ru.solution.models.Owner;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/owners")
public class OwnerController {
    @Autowired
    private OwnerService service;

    @GetMapping
    public List<OwnerDto> findAllOwners() {
        return service.findAllOwners();
    }

    @PostMapping("save_owner")
    public OwnerDto saveOwner(@RequestBody Owner owner) {
        return service.saveOwner(owner);
    }

    @PostMapping("save_owners")
    public List<OwnerDto> saveOwners(@RequestBody List<Owner> owners) {
        return owners.stream()
                .map(service::saveOwner)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public OwnerDto findOwner(@PathVariable("id") Long id) {
        return service.findOwner(id);
    }

    @PutMapping("update_owner")
    public OwnerDto updateOwner(@RequestBody Owner owner) {
        return service.updateOwner(owner);
    }

    @DeleteMapping("delete_owner/{id}")
    public void deleteOwner(@PathVariable("id") Long id) {
        service.deleteOwner(id);
    }
}

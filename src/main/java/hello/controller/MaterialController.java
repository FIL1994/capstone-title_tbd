package hello.controller;

import hello.EmptyJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import hello.model.*;
import hello.repository.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(path = "material")
public class MaterialController {
    @Autowired
    private MaterialRepository materialRepository;

    @PostMapping("")
    Material createMaterial(@Valid @RequestBody Material material) {
        return materialRepository.save(material);
    }

    @GetMapping("{id}")
    public ResponseEntity<Material> getMaterialById(@PathVariable(value = "id") Long materialId) {
        Optional<Material> optionalMaterial = materialRepository.findById(materialId);
        if (!optionalMaterial.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Material material = optionalMaterial.get();
        return ResponseEntity.ok().body(material);
    }

    @PutMapping("{id}")
    public ResponseEntity<Material> updateMaterial(@PathVariable(value = "id") Long materialId,
                                                   @Valid @RequestBody Material materialDetails) {
        Optional<Material> optionalCustomer = materialRepository.findById(materialId);
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Material material = optionalCustomer.get();

        Material updatedCustomer = materialRepository.save(material.merge(materialDetails));
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteMaterial(@PathVariable(value = "id") Long materialId) {
        Optional<Material> optionalMaterial = materialRepository.findById(materialId);
        if (!optionalMaterial.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Material material = optionalMaterial.get();

        materialRepository.delete(material);
        return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.OK);
    }
}

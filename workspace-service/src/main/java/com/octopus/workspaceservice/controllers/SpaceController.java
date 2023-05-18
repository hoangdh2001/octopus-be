package com.octopus.workspaceservice.controllers;

import com.octopus.workspaceservice.dtos.request.SpaceRequest;
import com.octopus.workspaceservice.models.Space;
import com.octopus.workspaceservice.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/spaces")
@CrossOrigin
@RequiredArgsConstructor
public class SpaceController {
    private final SpaceService spaceService;

//    @Operation(summary = "Create space", description = "Create space")
//    @PostMapping("/createSpace")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Create space"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized")
//    })
//    public ResponseEntity<Space> createSpace(@Valid @RequestBody SpaceRequest spaceRequest){
//        Space space = new Space();
//        space.setName(spaceRequest.getName());
//        space.setStatus(true);
//        space.setCreatedDate(spaceRequest.getCreateDate());
//        space.setUpdatedDate(spaceRequest.getUpdateDate());
//
//        return ResponseEntity.ok().body(spaceService.createSpace(space));
//    }
//
//    @Operation(summary = "Update Space", description = "Update space")
//    @PutMapping("/{space_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Update space"),
//            @ApiResponse(responseCode = "402", description = "Unauthorized")
//    })
//    public ResponseEntity<Space> updateSpace(@RequestBody SpaceRequest spaceRequest, @PathVariable("space_id") UUID id){
//        Optional<Space> space = spaceService.findById(id);
//        space.get().setName(spaceRequest.getName());
//        space.get().setStatus(true);
//        space.get().setCreatedDate(spaceRequest.getCreateDate());
//        space.get().setUpdatedDate(spaceRequest.getUpdateDate());
//
//        return ResponseEntity.ok().body(spaceService.updateSpace(space.get()));
//    }
//
//    @Operation(summary = "Delete space", description = "Delete space")
//    @DeleteMapping("/{space_id}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "202", description = "Delete space"),
//            @ApiResponse(responseCode = "403", description = "Unauthorized")
//    })
//    public ResponseEntity<?> deleteWorkspace(@PathVariable("space_id") UUID id) {
//        spaceService.deleteSpace(id);
//        return ResponseEntity.ok().body("space has been deleted successfully.");
//    }
//
//    @Operation(summary = "Find workspace by keywords", description = "Find workspace by keywords")
//    @GetMapping("/{key}")
//    @ApiResponses({
//            @ApiResponse(responseCode = "203", description = "Find workspace by keywords"),
//            @ApiResponse(responseCode = "405", description = "Not find workspace")
//    })
//    public ResponseEntity<Space> findByKeyWorks(@PathVariable("key") String key){
//        return ResponseEntity.ok().body(spaceService.searchSpace(key));
//    }
}

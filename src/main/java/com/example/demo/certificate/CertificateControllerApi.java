package com.example.demo.certificate;

import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.model.CertificateView;
import com.example.demo.certificate.role.CertificateStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Tag(name = "Certificate", description = "Certificate management APIs")
@SecurityRequirement(name = "bearerAuth") // Assuming auth is required for most operations
public interface CertificateControllerApi {

    @Operation(summary = "Upload a certificate file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully, returns file URL",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid file or upload error")
    })
    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<String> uploadCertificateFile(@Parameter(description = "Certificate file to upload") @RequestPart("file") MultipartFile file);

    @Operation(summary = "Add certificate details after uploading the file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Certificate added successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CertificateView.class))),
            @ApiResponse(responseCode = "400", description = "Invalid certificate data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PostMapping()
    ResponseEntity<CertificateView> addCertificate(Principal principal, @RequestBody CertificateRequest request);

    @Operation(summary = "Get certificates for the currently authenticated doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved certificates",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class, subTypes = {CertificateView.class}))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping()
    ResponseEntity<List<CertificateView>> getMyCertificates(Principal principal);

    @Operation(summary = "Update certificate status (for Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid status or certificate ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only Admins can update status"),
            @ApiResponse(responseCode = "404", description = "Certificate not found")
    })
    @PatchMapping("/{id}")
    ResponseEntity<String> updateCertificateStatus(@PathVariable Long id, @RequestParam CertificateStatus status);

    @Operation(summary = "Get all certificates for a specific doctor by Doctor ID (for Admin/Public)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved certificates",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class, subTypes = {CertificateView.class}))),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{doctorId}")
    ResponseEntity<List<CertificateView>> getDoctorCertificatesById(@PathVariable Long doctorId);

    @Operation(summary = "Delete a certificate (for Owner Doctor or Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate deleted successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not owner or Admin"),
            @ApiResponse(responseCode = "404", description = "Certificate not found"),
            @ApiResponse(responseCode = "500", description = "Error deleting file from storage")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteCertificate(Principal principal, @PathVariable Long id);

    @Operation(summary = "Get certificates filtered by status (for Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved certificates",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class, subTypes = {CertificateView.class}))),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only Admins can access this")
    })
    @GetMapping("/status")
    ResponseEntity<List<CertificateView>> getCertificatesByStatus(@RequestParam CertificateStatus status);
}

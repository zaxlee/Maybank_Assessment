package com.zax.maybank_assessment.web;

import com.zax.maybank_assessment.dto.TransactionDto;
import com.zax.maybank_assessment.dto.UpdateDescriptionRequest;
import com.zax.maybank_assessment.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    /** GET list with pagination + optional filters. */
    @GetMapping
    public Page<TransactionDto> list(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) List<String> accountNumbers,
            @RequestParam(required = false) String description,
            Pageable pageable) {

        var accs = new ArrayList<String>();
        if (accountNumber != null && !accountNumber.isBlank()) accs.add(accountNumber);
        if (accountNumbers != null && !accountNumbers.isEmpty()) accs.addAll(accountNumbers);

        return service.search(customerId, accs, description, pageable);
    }

    /** GET one by id; returns current version as ETag. */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> get(@PathVariable Long id) {
        TransactionDto dto = service.get(id);
        return ResponseEntity.ok()
                .eTag("\"" + dto.getVersion() + "\"")
                .body(dto);
    }

    /** PUT description; requires If-Match header with the current version. */
    @PutMapping("/{id}/description")
    public ResponseEntity<TransactionDto> updateDesc(
            @PathVariable Long id,
            @RequestHeader("If-Match") String ifMatch,
            @RequestBody @Valid UpdateDescriptionRequest body) {

        long expectedVersion = Long.parseLong(ifMatch.replace("\"", ""));
        TransactionDto dto = service.updateDescription(id, expectedVersion, body.getDescription());
        return ResponseEntity.ok()
                .eTag("\"" + dto.getVersion() + "\"")
                .body(dto);
    }
}
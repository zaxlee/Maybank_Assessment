package com.zax.maybank_assessment.service;

import com.zax.maybank_assessment.domain.Transaction;
import com.zax.maybank_assessment.dto.TransactionDto;
import com.zax.maybank_assessment.dto.TransactionMapper;
import com.zax.maybank_assessment.repo.TransactionRepository;
import com.zax.maybank_assessment.repo.TransactionSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repo;

    /** Paginated search with optional filters (customerId, accountNumbers, description). */
    @Transactional(readOnly = true)
    public Page<TransactionDto> search(
            String customerId, List<String> accountNumbers, String description, Pageable pageable) {

        return repo.findAll(TransactionSpecs.filter(customerId, accountNumbers, description), pageable)
                .map(TransactionMapper::toDto);
    }

    /** Get one transaction or throw NotFoundException. */
    @Transactional(readOnly = true)
    public TransactionDto get(Long id) {
        Transaction t = repo.findById(id).orElseThrow(() -> new NotFoundException(id));
        return TransactionMapper.toDto(t);
    }

    /**
     * Update 'description' using optimistic locking.
     * Client must pass the current version (from ETag) via If-Match.
     * If versions differ, a ConcurrencyException is thrown.
     */
    @Transactional
    public TransactionDto updateDescription(Long id, long expectedVersion, String newDesc) {
        Transaction t = repo.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (!Long.valueOf(expectedVersion).equals(t.getVersion())) {
            throw new ConcurrencyException(expectedVersion, t.getVersion());
        }

        t.setDescription(newDesc);
        Transaction saved = repo.saveAndFlush(t); // @Version increments automatically
        return TransactionMapper.toDto(saved);
    }

    // --- small, self-explanatory exceptions (mapped to HTTP later in the controller) ---

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(Long id) { super("Transaction not found: " + id); }
    }

    public static class ConcurrencyException extends RuntimeException {
        public ConcurrencyException(long expected, Long current) {
            super("Version mismatch (If-Match=" + expected + ", current=" + current + ")");
        }
    }
}
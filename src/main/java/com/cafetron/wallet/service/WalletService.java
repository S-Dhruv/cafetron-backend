package com.cafetron.wallet.service;

import com.cafetron.wallet.dto.PagedTransactionDto;
import com.cafetron.wallet.dto.TransactionResponseDto;
import com.cafetron.wallet.dto.WalletResponseDto;
import com.cafetron.wallet.entity.Transaction;
import com.cafetron.wallet.entity.TransactionType;
import com.cafetron.wallet.entity.Wallet;
import com.cafetron.wallet.exception.DuplicateRefundException;
import com.cafetron.wallet.exception.InsufficientFundsException;
import com.cafetron.wallet.exception.WalletNotFoundException;
import com.cafetron.wallet.repository.TransactionRepository;
import com.cafetron.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void debit(Long userId, BigDecimal amount, String reference) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(wallet.getBalance(), amount);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .orderId(parseOrderId(reference))
                .amount(amount)
                .type(TransactionType.DEBIT)
                .description("Order debit - ref: " + reference)
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void refund(Long userId, BigDecimal amount, String reference) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        Long orderId = parseOrderId(reference);

        if (orderId != null &&
                transactionRepository.existsByOrderIdAndType(
                        orderId, TransactionType.REFUND)) {
            throw new DuplicateRefundException(orderId);
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .orderId(orderId)
                .amount(amount)
                .type(TransactionType.REFUND)
                .description("Refund - ref: " + reference)
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional
    public void topUp(Long userId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Top-up amount must be positive");
        }

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .orderId(null)
                .amount(amount)
                .type(TransactionType.TOP_UP)
                .description("Manual top-up by admin")
                .build();

        transactionRepository.save(transaction);
    }

    public WalletResponseDto getWallet(Long userId) {

        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        return WalletResponseDto.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUser().getId())
                .balance(wallet.getBalance())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    public PagedTransactionDto getTransactions(Long userId, Pageable pageable) {

        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));

        Page<Transaction> page = transactionRepository
                .findByWallet_IdOrderByCreatedAtDesc(
                        wallet.getId(), pageable);

        List<TransactionResponseDto> transactions = page.getContent()
                .stream()
                .map(this::mapToTransactionDto)
                .collect(Collectors.toList());

        return PagedTransactionDto.builder()
                .transactions(transactions)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    private TransactionResponseDto mapToTransactionDto(Transaction t) {
        return TransactionResponseDto.builder()
                .id(t.getId())
                .type(t.getType())
                .amount(t.getAmount())
                .description(t.getDescription())
                .orderId(t.getOrderId())
                .createdAt(t.getCreatedAt())
                .build();
    }

    private Long parseOrderId(String reference) {
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException(
                    "Reference cannot be null or blank");
        }
        try {
            return Long.parseLong(reference);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Reference must be a numeric order ID, got: " + reference);
        }
    }
}
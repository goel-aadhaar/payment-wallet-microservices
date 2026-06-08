package com.payment_wallet.transaction_service.service;

import com.payment_wallet.transaction_service.client.WalletClient;
import com.payment_wallet.transaction_service.dto.HoldResponse;
import com.payment_wallet.transaction_service.dto.TransactionRequest;
import com.payment_wallet.transaction_service.dto.WalletResponse;
import com.payment_wallet.transaction_service.entity.Transaction;
import com.payment_wallet.transaction_service.kafka.KafkaEventProducer;
import com.payment_wallet.transaction_service.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock TransactionRepository transactionRepository;
    @Mock KafkaEventProducer kafkaEventProducer;
    @Mock WalletClient walletClient;
    @InjectMocks TransactionServiceImpl service;

    private TransactionRequest request() {
        return TransactionRequest.builder().senderId(1L).receiverId(2L).amount(100.0).build();
    }

    private void echoSave() {
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            if (t.getId() == null) t.setId(99L);
            return t;
        });
    }

    private HoldResponse hold() {
        return HoldResponse.builder().holdReference("H1").amount(100L).status("PLACED").build();
    }

    @Test
    void happyPath_capturesCredits_marksSuccess_andPublishesEvent() {
        echoSave();
        when(walletClient.placeHold(any())).thenReturn(hold());
        when(walletClient.getWallet(2L)).thenReturn(new WalletResponse());
        when(walletClient.capture(any())).thenReturn(new WalletResponse());
        when(walletClient.credit(any())).thenReturn(new WalletResponse());

        Transaction result = service.createTransaction(request());

        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        verify(kafkaEventProducer).sendTransactionEvent(anyString(), any(Transaction.class));
        verify(walletClient, never()).release(any());
    }

    @Test
    void receiverWalletMissing_releasesHold_marksFailed_noEvent() {
        echoSave();
        when(walletClient.placeHold(any())).thenReturn(hold());
        when(walletClient.getWallet(2L)).thenReturn(null);

        Transaction result = service.createTransaction(request());

        assertThat(result.getStatus()).isEqualTo("FAILED");
        verify(walletClient).release(any());
        verify(walletClient, never()).capture(any());
        verify(kafkaEventProducer, never()).sendTransactionEvent(anyString(), any());
    }

    @Test
    void creditFails_refundsSender_marksFailed() {
        echoSave();
        when(walletClient.placeHold(any())).thenReturn(hold());
        when(walletClient.getWallet(2L)).thenReturn(new WalletResponse());
        when(walletClient.capture(any())).thenReturn(new WalletResponse());
        // First credit (to receiver) fails; second credit (refund to sender) succeeds.
        when(walletClient.credit(any()))
                .thenThrow(new RuntimeException("credit failed"))
                .thenReturn(new WalletResponse());

        Transaction result = service.createTransaction(request());

        assertThat(result.getStatus()).isEqualTo("FAILED");
        verify(walletClient, times(2)).credit(any()); // receiver attempt + sender refund
        verify(kafkaEventProducer, never()).sendTransactionEvent(anyString(), any());
    }
}

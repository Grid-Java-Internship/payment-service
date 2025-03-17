package com.internship.payment_service.service;


import com.internship.payment_service.exception.NotFoundException;
import com.internship.payment_service.mapper.TransactionMapper;
import com.internship.payment_service.model.Status;
import com.internship.payment_service.model.Transaction;
import com.internship.payment_service.model.UserBalance;
import com.internship.payment_service.modelDTO.TransactionDTO;
import com.internship.payment_service.repository.TransactionRepository;
import com.internship.payment_service.repository.UserBalanceRepository;
import com.internship.payment_service.response.TransactionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserBalanceRepository userBalanceRepository;



    @Override
    @Transactional
    public TransactionResponse deposit(TransactionDTO transactionDTO) {
        return processTransaction(transactionDTO, PaymentAction.DEPOSIT);
    }


    @Override
    @Transactional
    public TransactionResponse withdraw(TransactionDTO transactionDTO) {
        return processTransaction(transactionDTO, PaymentAction.WITHDRAW);
    }

    /**
     * This method processes a transaction based on the type of transaction (DEPOSIT, WITHDRAW).
     * It checks if the user has enough balance for the transaction and if not, it will throw a
     * {@link NotFoundException} with a message indicating that the user has insufficient balance.
     * If the transaction is successful, it will add the transaction to the database and return a
     * {@link TransactionResponse} with the transaction details and a success message.
     * @param transactionDTO The transaction to be processed
     * @param paymentAction The type of transaction (DEPOSIT, WITHDRAW)
     * @return A {@link TransactionResponse} with the transaction details and a success message
     */
    private TransactionResponse processTransaction(TransactionDTO transactionDTO, PaymentAction paymentAction) {
        UserBalance userBalance = userBalanceRepository.findById(transactionDTO.getUserBalance().getUserId())
                .orElseThrow(() -> new NotFoundException("User with id: " + transactionDTO.getUserBalance().getUserId() + " not found!!"));

        Status status = (paymentAction == PaymentAction.DEPOSIT)
                ? processStatusType(transactionDTO.getAmount())
                : processStatusType(transactionDTO.getAmount(), userBalance.getBalance());

        transactionDTO.setStatus(status);
        log.info("{}", transactionDTO);

        if (transactionDTO.getStatus() == Status.COMPLETED || transactionDTO.getStatus() == Status.ON_HOLD) {
            double newBalance = (paymentAction == PaymentAction.DEPOSIT)
                    ? userBalance.getBalance() + transactionDTO.getAmount()
                    : userBalance.getBalance() - transactionDTO.getAmount();
            userBalance.setBalance(newBalance);
        }

        Transaction transaction = transactionMapper.dtoToEntity(transactionDTO);
        transaction.setUserBalance(userBalance);

        log.info("{}", transaction);

        transaction = transactionRepository.save(transaction);
        String message = getMessage(transactionDTO, paymentAction, transaction);
        return TransactionResponse.builder().returnMessage(message).build();
    }


    /**
     * Generate a message based on the transaction details and the payment action.
     * If the transaction is successful, it will return a message indicating that the transaction was
     * successful and the new balance of the user. If the transaction status is ON_HOLD it will
     * return a message indicating that the transaction is on hold. If the transaction is rejected,
     * it will return a message indicating that the transaction was rejected due to insufficient balance.
     * @param transactionDTO The transaction details
     * @param paymentAction The type of transaction (DEPOSIT, WITHDRAW)
     * @param transaction The transaction entity
     * @return A message based on the transaction details and the payment action
     */
    private static String getMessage(TransactionDTO transactionDTO, PaymentAction paymentAction, Transaction transaction) {
        String message;

        if (transactionDTO.getStatus() == Status.COMPLETED || transactionDTO.getStatus() == Status.ON_HOLD) {
            if (paymentAction == PaymentAction.DEPOSIT) {
                message = "You have successfully added " + transactionDTO.getAmount() + " credits. New balance: " + transaction.getUserBalance().getBalance();
            } else {
                message = "You have successfully withdrawn " + transactionDTO.getAmount() + " credits. New balance: " + transaction.getUserBalance().getBalance();
            }
        } else {
            message = "Transaction rejected. Insufficient funds in your account.";
        }
        return message;
    }


    @Override
    public Status processStatusType(Double amount) {
        return (amount > 100000) ? Status.ON_HOLD : Status.COMPLETED;
    }

    @Override
    public Status processStatusType(Double amountToWithdraw, Double userBalance) {
        return (amountToWithdraw > userBalance) ? Status.REJECTED : Status.COMPLETED;
    }

    private enum PaymentAction {
        DEPOSIT, WITHDRAW
    }
}

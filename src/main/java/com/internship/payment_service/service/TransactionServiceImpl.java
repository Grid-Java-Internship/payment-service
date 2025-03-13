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


        Status status = processTransactionType(transactionDTO.getAmount());
        transactionDTO.setStatus(status);
        log.info("{}", transactionDTO);

        UserBalance userBalance = userBalanceRepository.findById(transactionDTO.getUserBalance().getUserId())
                .orElseThrow(() -> new NotFoundException("User with id: " + transactionDTO.getUserBalance().getUserId() + " not found!!"));

//        if (status == Status.ON_HOLD) {
//            //TO DO
//            //CALL NOTIFICATION SERVICE
//            //...
//        }
        userBalance.setBalance(transactionDTO.getAmount() + userBalance.getBalance());

        Transaction transaction = transactionMapper.dtoToEntity(transactionDTO);
        transaction.setUserBalance(userBalance);

        log.info("{}", transaction);

        transaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .returnMessage("You have successfully added " + transactionDTO.getAmount() + " credits. New balance: " + transaction.getUserBalance().getBalance())
                .build();


    }

    private Status processTransactionType(Double amount) {

        if (amount > 100000)
            return Status.ON_HOLD;
        return Status.COMPLETED;
    }
}

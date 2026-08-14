package com.example.bankcards.exception.card;

import com.example.bankcards.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public sealed abstract class CardManagementException
    extends CommonException
    permits BadTransferAmoundException, CardNotFoundException, EntityNotFoundException, NotActiveDebitCardException, NotActiveDepositCardException, NotEnoughAmountException, SameCardException, UserAlreadyExistsException, UserNotFoundException, WrongCardException {

    public CardManagementException(String message, HttpStatus status) {
        super(message, status);
    }

}

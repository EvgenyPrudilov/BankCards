package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.service.model.*;
import com.example.bankcards.util.MaskingUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Named("maskCardNumber")
    default String maskCardNumber(String cardNumber) {
        return MaskingUtils.maskCardNumber(cardNumber);
    }

    CreateCardResponseDto toDto(CardEntity card);

    CreateCardRequest toDomain(CreateCardRequestDto request);

    UpdateCardStatusRequest toDomain(UpdateCardStatusRequestDto requestDto);

    DeleteCardRequest toDomain(DeleteCardRequestDto requestDto);

    GetCardsRequest toDomain(GetCardsRequestDto requestDto);

    GetCardBalanceResponseDto toDto(GetCardBalanceResponse request);

    GetCardBalanceRequest toDomain(GetCardBalanceRequestDto requestDto);

    BlockCardRequest toDomain(BlockCardRequestDto requestDto);

    TransferRequest toDomain(TransferRequestDto requestDto);

    default GetCardsResponseDto toDto(Page<CardEntity> page) {
        if (page == null) {
            return null;
        }
        GetCardsResponseDto response = new GetCardsResponseDto();
        response.setCards(toCardResponseDtoList(page.getContent()));
        return response;
    }

    List<GetCardsRequestDto> toCardResponseDtoList(List<CardEntity> content);

}

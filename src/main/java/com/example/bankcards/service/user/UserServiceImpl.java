package com.example.bankcards.service.user;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.model.GetUsersRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<UserEntity> searchUsers(GetUsersRequest request, Pageable pageable) {
        String userNameParam = request.getUserName();
        String holderNameParam = request.getHolderName();

        String userName = (userNameParam != null && !userNameParam.isBlank()) ? userNameParam : null;
        String holderName = (holderNameParam != null && !holderNameParam.isBlank()) ? holderNameParam : null;

        return userRepository.searchByUserNameAndCardHolderName(userName, holderName, pageable);
    }
}

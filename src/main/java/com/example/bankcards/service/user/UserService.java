package com.example.bankcards.service.user;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.service.model.GetUsersRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserEntity> searchUsers(GetUsersRequest request, Pageable pageable);

}

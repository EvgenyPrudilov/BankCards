package com.example.bankcards.controller.admin;

import com.example.bankcards.controller.docs.admin.UserControllerDocs;
import com.example.bankcards.dto.user.GetUsersRequestDto;
import com.example.bankcards.dto.user.GetUsersResponseDto;
import com.example.bankcards.service.ServicesGate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserManagingController implements UserControllerDocs {

    private final ServicesGate servicesGate;

    @PostMapping("/search")
    @Override
    public ResponseEntity<GetUsersResponseDto> searchUsers(
        @Valid @RequestBody GetUsersRequestDto requestDto,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        GetUsersResponseDto users = servicesGate.searchUsers(requestDto, pageable);
        return ResponseEntity.ok(users);
    }
}

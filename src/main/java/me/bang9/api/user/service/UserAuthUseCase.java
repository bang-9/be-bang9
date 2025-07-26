package me.bang9.api.user.service;

import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserAuthUseCase {
    
    /**
     * Create a new user with EMAIL provider
     * @param request UserCreateRequest containing email, password, nickname, and provider
     * @return UserResponse of the created user
     * @throws IllegalArgumentException if email already exists or validation fails
     */
    UserResponse createUser(UserCreateRequest request);
    
    /**
     * Get all users in the system
     * @return List of all users as UserResponse
     */
    List<UserResponse> getAllUsers();
    
    /**
     * Get a specific user by ID
     * @param userId UUID of the user to retrieve
     * @return UserResponse of the requested user
     * @throws IllegalArgumentException if user not found
     */
    UserResponse getUserById(UUID userId);
    
    /**
     * Update user information (nickname only for now)
     * @param userId UUID of the user to update
     * @param request UserUpdateRequest containing new nickname
     * @return UserResponse of the updated user
     * @throws IllegalArgumentException if user not found or nickname validation fails
     */
    UserResponse updateUser(UUID userId, UserUpdateRequest request);
    
    /**
     * Soft delete a user by setting deleted flag
     * @param userId UUID of the user to delete
     * @throws IllegalArgumentException if user not found or already deleted
     */
    void softDeleteUser(UUID userId);
}

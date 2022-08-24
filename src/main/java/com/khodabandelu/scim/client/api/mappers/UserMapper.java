package com.khodabandelu.scim.client.api.mappers;

import com.khodabandelu.scim.client.api.dto.UserDto;
import com.khodabandelu.scim.client.domains.user.User;
import org.mapstruct.Mapper;

import java.util.List;


/**
 * This class use to map {@link User} to {@link UserDto} and vice versa.
 *
 * @author Mahdi Khodabandelu
 */

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto dto);

    UserDto toDto(User user);
    List<User> toEntity(List<UserDto> dtoList);

    List<UserDto> toDto(List<User> entityList);
}

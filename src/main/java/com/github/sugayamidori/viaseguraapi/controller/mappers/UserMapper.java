package com.github.sugayamidori.viaseguraapi.controller.mappers;

import com.github.sugayamidori.viaseguraapi.controller.dto.SaveUserDTO;
import com.github.sugayamidori.viaseguraapi.controller.dto.UserDTO;
import com.github.sugayamidori.viaseguraapi.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO dto);

    User toEntity(SaveUserDTO dto);

    UserDTO toDTO(User user);
}

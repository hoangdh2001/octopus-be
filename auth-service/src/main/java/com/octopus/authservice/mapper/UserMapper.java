package com.octopus.authservice.mapper;


import com.octopus.authservice.model.User;
import com.octopus.dtomodels.UserDTO;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", expression = "java(convertIdToString(user.getId()))")
    UserDTO mapToUserDTO(User user);

    @InheritConfiguration(name = "mapToUserDTO")
    List<UserDTO> mapListUserToUserDTO(List<User> users);

    @Mapping(target = "id", expression = "java(convertStringToID(userDTO.getId()))")
    User mapUserDTOToUser(UserDTO userDTO);

    default String convertIdToString(UUID id) {
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    default UUID convertStringToID(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return UUID.fromString(id);
    }


}

package com.octopus.authservice.mapper;


import com.octopus.authservice.model.Connection;
import com.octopus.authservice.model.Device;
import com.octopus.authservice.model.User;
import com.octopus.dtomodels.DeviceDTO;
import com.octopus.dtomodels.UserDTO;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
@Transactional
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", expression = "java(convertIdToString(user.getId()))")
    @Mapping(target = "connections", expression = "java(convertConnectionToListString(user.getConnections()))")
    @Mapping(target = "devices", expression = "java(mapListDeviceToDeviceDTO(user.getDevices()))")
    UserDTO mapToUserDTO(User user);

    @InheritConfiguration(name = "mapToUserDTO")
    List<UserDTO> mapListUserToUserDTO(List<User> users);

    @Mapping(target = "id", expression = "java(convertStringToID(userDTO.getId()))")
    @Mapping(target = "connections", expression = "java(convertListStringToConnections(userDTO.getConnections()))")
    @Mapping(target = "devices",expression = "java(mapDeviceDTOToListDevice(userDTO.getDevices()))")
    User mapUserDTOToUser(UserDTO userDTO);

    DeviceDTO mapToDeviceDTO(Device device);

    Device mapDTOToDevice(DeviceDTO deviceDTO);

    @InheritConfiguration(name = "mapToDeviceDTO")
    List<DeviceDTO> mapListDeviceToDeviceDTO(List<Device> devices);

    @InheritConfiguration(name = "mapDTOToDevice")
    List<Device> mapDeviceDTOToListDevice(List<DeviceDTO> devices);



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

    default List<String> convertConnectionToListString(List<Connection> connections) {
        if (connections == null) {
            return null;
        }
        List<String> connectionsID = new ArrayList<>();
        connections.forEach((connection -> {
            connectionsID.add(connection.getConnectionID());
        }));
        return connectionsID;
    }

    default List<Connection> convertListStringToConnections(List<String> connectionsID) {
        if (connectionsID == null) {
            return null;
        }
        List<Connection> connections = new ArrayList<>();
        connectionsID.forEach((connectionID -> {
            Connection connection = new Connection(connectionID);
            connections.add(connection);
        }));
        return connections;
    }


}

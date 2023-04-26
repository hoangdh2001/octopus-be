package com.octopus.authservice.repositories;

import com.octopus.authservice.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findDevicesByUserIdAndDisabledIsFalse(UUID user);

    @Modifying
    @Query("update Device set disabled = false where deviceID = :deviceID")
    boolean disabledDevice(String deviceID);
}

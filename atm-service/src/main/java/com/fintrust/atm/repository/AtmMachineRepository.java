package com.fintrust.atm.repository;

import com.fintrust.atm.entity.AtmMachine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtmMachineRepository extends JpaRepository<AtmMachine, Long> {
}

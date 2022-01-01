package com.zelazobeton.cognitiveexercisesmemory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercisesmemory.domain.Portfolio;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}

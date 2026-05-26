package com.br.shizen.certificateemitter.repository;

import com.br.shizen.certificateemitter.entity.Take;
import com.br.shizen.certificateemitter.entity.id.TakeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TakeRepository  extends JpaRepository<Take, TakeId> {
}

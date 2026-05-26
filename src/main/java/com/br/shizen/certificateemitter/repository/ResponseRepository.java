package com.br.shizen.certificateemitter.repository;

import com.br.shizen.certificateemitter.entity.Response;
import com.br.shizen.certificateemitter.entity.id.ResponseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends JpaRepository<Response, ResponseId> {
}

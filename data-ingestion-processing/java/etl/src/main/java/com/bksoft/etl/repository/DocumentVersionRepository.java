package com.bksoft.etl.repository;

import com.bksoft.etl.entities.DocumentVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersionEntity, Long> {

    Optional<DocumentVersionEntity> findByDocumentIdAndVersion(Long documentId, Integer version);

    List<DocumentVersionEntity> findByDocumentIdOrderByVersionDesc(Long documentId);

    Optional<DocumentVersionEntity> findTopByDocumentIdOrderByVersionDesc(Long documentId);

    Optional<DocumentVersionEntity> findByFileHash(String fileHash);

    boolean existsByDocumentIdAndChecksum(Long documentId, String checksum);

    boolean existsByDocumentIdAndFileHash(String documentId, String fileHash);

    boolean existsByFileHash(String fileHash);
}

package org.example.Reposistories;

import org.example.Entities.TerminalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalDetailsRepository extends JpaRepository<TerminalDetails, Integer> {

}

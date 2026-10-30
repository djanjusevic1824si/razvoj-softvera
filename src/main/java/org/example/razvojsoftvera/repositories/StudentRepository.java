package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

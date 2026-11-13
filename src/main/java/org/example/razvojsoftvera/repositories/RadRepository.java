package org.example.razvojsoftvera.repositories;

import jakarta.persistence.LockModeType;
import org.example.razvojsoftvera.model.Rad;
import org.example.razvojsoftvera.model.StatusRada;
import org.example.razvojsoftvera.responses.MonitoringDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RadRepository extends JpaRepository<Rad, Long> {

    @Override
    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    List<Rad> findAll();

    @Override
    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    Page<Rad> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    Optional<Rad> findById(Long id);

    boolean existsByPrijava_Id(Long prijavaId);

    boolean existsByGrupa_Id(Long grupaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Rad r where r.id = :id")
    Optional<Rad> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"prijava", "grupa", "ocenioNastavnik"})
    Page<Rad> findAllByPrijava_Ispit_Id(
            Long ispitId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"prijava", "grupa.termin.ispit.predmet", "grupa.zadatak"})
    Optional<Rad> findByPrijava_Id(Long prijavaId);

    @Query("""
        select r from Rad r
        where r.statusRada = :status
          and r.grupa.termin.krajIzrade < :granica
        """)
    List<Rad> nadjiIstekleRadove(StatusRada status, LocalDateTime granica);

    @Query("""
    select new org.example.razvojsoftvera.responses.MonitoringDto(
        si.id, sp.oznaka, si.broj, si.godinaUpisa, st.ime, st.prezime,
        r.id, g.naziv, r.ucionica, r.statusRada, r.vremePreuzimanja,
        (select max(p.vreme) from Predaja p
            where p.rad = r and p.tipPredaje = org.example.razvojsoftvera.model.TipPredaje.KONACNA),
        (select count(p) from Predaja p where p.rad = r))
    from Prijava pr
    join pr.studentIndeks si
    join si.student st
    join si.studijskiProgram sp
    left join Rad r on r.prijava = pr
    left join r.grupa g
    where pr.ispit.id = :ispitId
      and (:grupaId is null or g.id = :grupaId)
      and (:status is null or r.statusRada = :status)
    order by st.prezime, st.ime
    """)
    List<MonitoringDto> monitoring(@Param("ispitId") Long ispitId,
                                   @Param("grupaId") Long grupaId,
                                   @Param("status") StatusRada status);

    @Query("""
    select case when exists (
        select 1 from Rad r
        join r.prijava pr
        join pr.studentIndeks si
        join si.student st
        where pr.ispit.id = :ispitId
          and st.korisnik.id = :korisnikId
    ) then true else false end
    """)
    boolean existsZaIspitIKorisnika(@Param("ispitId") Long ispitId,
                                    @Param("korisnikId") Long korisnikId);



    @Query("""
    select r from Rad r
    join r.prijava pr
    join pr.studentIndeks si
    where pr.ispit.id = :ispitId
      and si.student.id = :studentId
    """)
    Optional<Rad> findByIspitIdAndStudentId(@Param("ispitId") Long ispitId,
                                            @Param("studentId") Long studentId);
}
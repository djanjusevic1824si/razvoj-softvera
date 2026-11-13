package org.example.razvojsoftvera.repositories;

import org.example.razvojsoftvera.model.Predaja;
import org.example.razvojsoftvera.model.TipPredaje;
import org.example.razvojsoftvera.responses.ResenjeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PredajaRepository extends JpaRepository<Predaja, Long> {

    @Override
    @EntityGraph(attributePaths = "rad")
    List<Predaja> findAll();

    @Override
    @EntityGraph(attributePaths = "rad")
    Page<Predaja> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "rad")
    Optional<Predaja> findById(Long id);

    boolean existsByRad_IdAndTipPredaje(
            Long radId,
            TipPredaje tipPredaje
    );

    @Query("""
            select coalesce(max(p.redniBroj), 0)
            from Predaja p
            where p.rad.id = :radId
            """)
    Integer findMaxRedniBrojByRadId(@Param("radId") Long radId);

    @EntityGraph(attributePaths = "rad")
    Page<Predaja> findAllByRad_Id(
            Long radId,
            Pageable pageable
    );

    Optional<Predaja> findFirstByRad_IdOrderByRedniBrojDesc(Long radId);

    Optional<Predaja> findByRad_IdAndTipPredaje (Long radId, TipPredaje tipPredaje);


    @Query("select new org.example.razvojsoftvera.responses.ResenjeResponse(" +
            "            pd.id, r.id, sp.oznaka, si.broj, si.godinaUpisa, st.ime, st.prezime, g.naziv," +
            "            pd.putanjaResenja, pd.vreme, r.poeni, r.vremeOcenjivanja) from Predaja pd" +
            "        join pd.rad r" +
            "        join r.prijava pr" +
            "        join pr.studentIndeks si" +
            "        join si.student st" +
            "        join si.studijskiProgram sp" +
            "        join r.grupa g" +
            "        where pd.tipPredaje = org.example.razvojsoftvera.model.TipPredaje.KONACNA" +
            "          and pr.ispit.id = :ispitId" +
            "          and (:grupaId is null or g.id = :grupaId) order by g.naziv, st.prezime, st.ime, pd.id")
    List<ResenjeResponse> konacnePredajeSve(@Param("ispitId") Long ispitId, @Param("grupaId") Long grupaId);
}
package org.example.registerpolo.Repository;



import org.example.registerpolo.Entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhuyenMaiRepo extends JpaRepository<KhuyenMai, Integer> {
    boolean existsByMaKM(String maKM);
}

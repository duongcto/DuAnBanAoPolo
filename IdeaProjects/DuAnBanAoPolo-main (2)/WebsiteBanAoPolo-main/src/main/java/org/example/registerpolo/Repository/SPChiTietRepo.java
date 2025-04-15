package org.example.registerpolo.Repository;

import org.example.registerpolo.Entity.SPChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPChiTietRepo extends JpaRepository<SPChiTiet, Integer> {

    @Query("SELECT sp FROM SPChiTiet sp WHERE " +
            "(:keyword IS NULL OR LOWER(sp.maSPCT) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(sp.sanPham.ten) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:mau IS NULL OR sp.mauSac.id = :mau) AND " +
            "(:size IS NULL OR sp.kichThuoc.id = :size)")
    List<SPChiTiet> filterSanPham(@Param("keyword") String keyword,
                                  @Param("mau") Integer mau,
                                  @Param("size") Integer size);
}



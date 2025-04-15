package org.example.registerpolo.Repository;


import org.example.registerpolo.Entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet, Integer> {
    List<HoaDonChiTiet> findByHoaDon_Id(Integer idHoaDon);
    HoaDonChiTiet findByHoaDon_IdAndSpChiTiet_Id(Integer hoaDonId, Integer spId);

}

package org.example.registerpolo.Repository;

import org.example.registerpolo.Entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    KhachHang findByEmail(String email);
    @Query("SELECT kh FROM KhachHang kh ORDER BY kh.id DESC LIMIT 1")
    KhachHang findLastKhachHang();
    KhachHang findBySdt(String sdt);


}

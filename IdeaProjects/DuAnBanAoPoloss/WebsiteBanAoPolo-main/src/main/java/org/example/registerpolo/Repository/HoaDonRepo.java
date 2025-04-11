package org.example.registerpolo.Repository;

import org.example.registerpolo.Entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.time.LocalDate;


@Repository
public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    @NonNull
    Page<HoaDon> findAll(@NonNull Pageable pageable);
    // Tìm kiếm hóa đơn theo khoảng thời gian
    Page<HoaDon> findByKhachHangSdtContaining(String sdt, @NonNull Pageable pageable);

    Page<HoaDon> findByNgayMuaHangBetween(LocalDate startDate, LocalDate endDate, @NonNull Pageable pageable);

    // Không cần thêm findById vì JpaRepository đã có sẵn

}


package org.example.registerpolo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ThongKeDoanhThu")
public class ThongKeDoanhThu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Ngay")
    private LocalDate ngay;

    @Column(name = "TongSoHoaDon")
    private Integer tongSoHoaDon;

    @Column(name = "TongDoanhThu")
    private BigDecimal tongDoanhThu;

    @Column(name = "TongSanPhamBanRa")
    private Integer tongSanPhamBanRa;
}


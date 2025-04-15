package org.example.registerpolo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "IdKhachHang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "IdNhanVien")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "IdKhuyenMai")
    private KhuyenMai khuyenMai;

    @Column(name = "NgayMuaHang")
    private LocalDate ngayMuaHang;

    @Column(name = "TongTien")
    private BigDecimal tongTien;

    @Column(name = "HinhThucThanhToan")
    private String hinhThucThanhToan; // Ví dụ: "Tiền mặt", "Chuyển khoản", "Thẻ"

    @Column(name = "GhiChu")
    private String ghiChu;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HoaDonChiTiet> hoaDonChiTietList;
}



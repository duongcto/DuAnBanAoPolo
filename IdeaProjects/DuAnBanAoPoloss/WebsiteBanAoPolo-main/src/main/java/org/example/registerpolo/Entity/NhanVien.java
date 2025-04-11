package org.example.registerpolo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "NhanVien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Ten", nullable = false)
    private String ten;

    @Column(name = "MaNV", nullable = false)
    private String maNV;

    @Column(name = "TenDangNhap", nullable = false)
    private String tenDangNhap;

    @Column(name = "MatKhau", nullable = false)
    private String matKhau;

    @ManyToOne
    @JoinColumn(name = "IdVaiTro")
    private VaiTro vaiTro;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}


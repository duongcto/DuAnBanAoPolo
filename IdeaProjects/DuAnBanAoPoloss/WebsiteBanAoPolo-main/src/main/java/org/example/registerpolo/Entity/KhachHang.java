package org.example.registerpolo.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "KhachHang") // 👈 Chỉ định tên bảng đúng trong DB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Ten")
    private String ten;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "Email")
    private String email;

    @Column(name = "MaKH")
    private String maKH;

    @Column(name = "TrangThai")
    private Boolean trangThai;
}

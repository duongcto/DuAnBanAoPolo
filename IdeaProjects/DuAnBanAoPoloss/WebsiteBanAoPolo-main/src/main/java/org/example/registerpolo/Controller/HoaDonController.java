package org.example.registerpolo.Controller;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.registerpolo.Entity.HoaDon;
import org.example.registerpolo.Entity.HoaDonChiTiet;
import org.example.registerpolo.Repository.HoaDonChiTietRepo;
import org.example.registerpolo.Repository.HoaDonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/hoa-don")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;

    @GetMapping("/view")
    public String locHoaDon(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Boolean filtered,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<HoaDon> hoaDonPage;

        if (Boolean.TRUE.equals(filtered)) {
            if (startDate == null || endDate == null) {
                model.addAttribute("errorMessage", "Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
                hoaDonPage = hoaDonRepo.findAll(pageable);
            } else {
                hoaDonPage = hoaDonRepo.findByNgayMuaHangBetween(startDate, endDate, pageable);
            }
        } else {
            hoaDonPage = hoaDonRepo.findAll(pageable);
        }

        model.addAttribute("hoaDonPage", hoaDonPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "hoaDon";
    }

    @GetMapping("/search")
    public String timKiemHoaDon(
            @RequestParam(required = false) String sdt,
            @RequestParam(required = false) String maHoaDon,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<HoaDon> hoaDonPage;

        if (sdt != null && !sdt.isEmpty()) {
            hoaDonPage = hoaDonRepo.findByKhachHangSdtContaining(sdt, pageable);
        } else if (maHoaDon != null && !maHoaDon.isEmpty()) {
            try {
                Integer id = Integer.parseInt(maHoaDon);
                Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(id);
                if (hoaDonOptional.isPresent()) {
                    List<HoaDon> list = Collections.singletonList(hoaDonOptional.get());
                    hoaDonPage = new PageImpl<>(list, pageable, 1);
                } else {
                    hoaDonPage = Page.empty(pageable);
                }
            } catch (NumberFormatException e) {
                model.addAttribute("errorMessage", "Mã hóa đơn không hợp lệ.");
                hoaDonPage = hoaDonRepo.findAll(pageable);
            }
        } else {
            hoaDonPage = hoaDonRepo.findAll(pageable);
        }

        model.addAttribute("hoaDonPage", hoaDonPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sdt", null);
        model.addAttribute("maHoaDon", null);
        return "hoaDon";
    }

    @GetMapping("/chi-tiet/{id}")
    public String hienThiChiTietHoaDon(@PathVariable("id") Integer id, Model model) {
        HoaDon hoaDon = hoaDonRepo.findById(id).orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietRepo.findByHoaDonId(id);

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hoaDonChiTietList", hoaDonChiTietList);

        return "/hoaDonChiTiet.html";
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<ByteArrayResource> exportHoaDonTheoId(@PathVariable("id") Integer id) throws IOException {
        Optional<HoaDon> optionalHoaDon = hoaDonRepo.findById(id);
        if (!optionalHoaDon.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        HoaDon hoaDon = optionalHoaDon.get();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("HoaDon");

        Row headerRow = sheet.createRow(0);
        String[] columns = {"Mã Hóa Đơn", "Khách Hàng", "SĐT", "Nhân Viên", "Ngày Mua", "Trạng Thái"};
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i]);
        }

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue("HD-" + hoaDon.getId());
        row.createCell(1).setCellValue(hoaDon.getKhachHang().getTen());
        row.createCell(2).setCellValue(hoaDon.getKhachHang().getSdt());
        row.createCell(3).setCellValue(hoaDon.getNhanVien().getTen());
        row.createCell(4).setCellValue(hoaDon.getNgayMuaHang().toString());
        row.createCell(5).setCellValue(hoaDon.getTrangThai() ? "Đã thanh toán" : "Đã hủy");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelFile = bos.toByteArray();

        ByteArrayResource resource = new ByteArrayResource(excelFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hoa_don_HD-" + id + ".xlsx")
                .contentLength(excelFile.length)
                .body(resource);
    }
}
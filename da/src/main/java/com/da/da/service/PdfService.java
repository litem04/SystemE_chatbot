package com.da.da.service;

import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public ByteArrayInputStream exportInvoicePdf(Order order, List<OrderDetail> orderDetails) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- HEADER ---
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("TECHGEAR - HOA DON MUA HANG", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // --- THÔNG TIN KHÁCH HÀNG (Dùng safeString để chống NullPointerException) ---
            Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 12);
            
            document.add(new Paragraph("Ma don hang: #" + safeString(order.getId()), fontInfo));
            document.add(new Paragraph("Ngay dat: " + safeString(order.getOrderDate()), fontInfo));
            document.add(new Paragraph("Khach hang: " + safeString(order.getCustomerName()), fontInfo));
            document.add(new Paragraph("SDT: " + safeString(order.getMobileNumber()), fontInfo));
            document.add(new Paragraph("Dia chi: " + safeString(order.getAddress()), fontInfo));
            document.add(new Paragraph("\n"));

            // --- BẢNG SẢN PHẨM ---
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 1, 2});

            addTableHeader(table, "San Pham");
            addTableHeader(table, "So Luong");
            addTableHeader(table, "Thanh Tien");

            if (orderDetails != null) {
                for (OrderDetail detail : orderDetails) {
                    // Tên SP
                    table.addCell(new PdfPCell(new Phrase(safeString(detail.getProductName()))));
                    
                    // Số lượng
                    String qty = (detail.getQuantity() != null) ? String.valueOf(detail.getQuantity()) : "0";
                    table.addCell(new PdfPCell(new Phrase(qty)));
                    
                    // Tính toán giá tiền an toàn
                    Double price = (detail.getPrice() != null) ? detail.getPrice() : 0.0;
                    Integer quantity = (detail.getQuantity() != null) ? detail.getQuantity() : 0;
                    double subTotal = price * quantity;
                    
                    // Format số tiền (bỏ số thập phân cho gọn: 100000)
                    String priceStr = String.format("%.0f", subTotal); 
                    table.addCell(new PdfPCell(new Phrase(priceStr)));
                }
            }
            document.add(table);

            // --- TỔNG CỘNG ---
            document.add(new Paragraph("\n"));
            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            
            // Xử lý tổng tiền (Cẩn thận vì productTotalPrice của bạn có thể là String có chữ 'đ' hoặc null)
            String totalStr = safeString(order.getProductTotalPrice());
            Paragraph pTotal = new Paragraph("Tong thanh toan: " + totalStr, fontTotal);
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(pTotal);
            
            // --- FOOTER ---
            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Cam on quy khach!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close(); // Quan trọng: Phải đóng document thì file mới hoàn chỉnh

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra Console để sửa
            return null; 
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Hàm phụ trợ check null
    private String safeString(Object obj) {
        return (obj != null) ? obj.toString() : "";
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(header);
    }
}
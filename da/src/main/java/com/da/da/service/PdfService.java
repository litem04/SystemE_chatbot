package com.da.da.service;

import com.da.da.entity.Order;
import com.da.da.entity.OrderDetail;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    public ByteArrayInputStream exportInvoicePdf(Order order, List<OrderDetail> orderDetails) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DecimalFormat df = new DecimalFormat("###,###,###");

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Header
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("TECHGEAR - HOA DON MUA HANG", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Customer Info
            Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Ma don hang: #" + safeString(order.getId()), fontInfo));
            document.add(new Paragraph("Ngay dat: " + safeString(order.getOrderDate()), fontInfo));
            document.add(new Paragraph("Khach hang: " + safeString(order.getCustomerName()), fontInfo));
            document.add(new Paragraph("SDT: " + safeString(order.getMobileNumber()), fontInfo));
            document.add(new Paragraph("Dia chi: " + safeString(order.getAddress()), fontInfo));
            document.add(new Paragraph("\n"));

            // Product Table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 1, 2});

            addTableHeader(table, "San Pham");
            addTableHeader(table, "So Luong");
            addTableHeader(table, "Thanh Tien");

            if (orderDetails != null) {
                for (OrderDetail detail : orderDetails) {
                    table.addCell(new PdfPCell(new Phrase(safeString(detail.getProductName()))));
                    int quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(quantity))));

                    BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
                    BigDecimal subTotal = detail.getTotalPrice() != null ? detail.getTotalPrice() : price.multiply(BigDecimal.valueOf(quantity));
                    table.addCell(new PdfPCell(new Phrase(df.format(subTotal) + " VND")));
                }
            }
            document.add(table);

            // Total
            document.add(new Paragraph("\n"));
            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            BigDecimal total = order.getProductTotalPrice() != null ? order.getProductTotalPrice() : BigDecimal.ZERO;
            Paragraph pTotal = new Paragraph("Tong thanh toan: " + df.format(total) + " VND", fontTotal);
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            document.add(pTotal);

            // Footer
            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Cam on quy khach da tin tuong mua hang!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            log.error("Lỗi khi xuất hóa đơn PDF cho đơn hàng #{}: ", order.getId(), e);
            return null;
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

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
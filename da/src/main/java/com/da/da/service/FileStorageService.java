package com.da.da.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    public String storeFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return null;
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file quá lớn. Tối đa 5MB.");
        }

        String originalName = file.getOriginalFilename();
        String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase() : "";

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Định dạng file không được hỗ trợ. Chỉ chấp nhận JPG, PNG, WEBP.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("MIME type file không hợp lệ: " + contentType);
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            if (is.read(header) >= 8) {
                String hex = bytesToHex(header).toUpperCase();
                boolean valid = false;
                if (ext.equals(".jpg") || ext.equals(".jpeg")) {
                    if (hex.startsWith("FFD8FF")) valid = true;
                } else if (ext.equals(".png")) {
                    if (hex.startsWith("89504E47")) valid = true;
                } else if (ext.equals(".webp")) {
                    if (hex.startsWith("52494646") && hex.substring(16).startsWith("57454250")) valid = true;
                }
                if (!valid) {
                    throw new IllegalArgumentException("Nội dung file không đúng chuẩn định dạng ảnh.");
                }
            } else {
                throw new IllegalArgumentException("File quá nhỏ hoặc bị hỏng.");
            }
        }

        String safeFileName = UUID.randomUUID() + ext;
        try (InputStream inputStream = file.getInputStream()) {
            Path targetPath = uploadPath.resolve(safeFileName);
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Lưu file thành công: {} -> {}", originalName, safeFileName);
        return safeFileName;
    }

    public void deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) return;
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("Đã xóa file: {}", filePath);
        } catch (IOException e) {
            log.warn("Không thể xóa file: {}", filePath, e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}

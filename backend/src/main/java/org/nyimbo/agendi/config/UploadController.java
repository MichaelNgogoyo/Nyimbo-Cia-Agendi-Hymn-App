package org.nyimbo.agendi.config;

import org.nyimbo.agendi.audit.AuditService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/uploads")
public class UploadController {
    @Value("${nyimbo.upload-dir:./uploads}") private String uploadDir;
    private final AuditService audit;
    public UploadController(AuditService audit) { this.audit = audit; }
    @PostMapping("/images") public Map<String,String> image(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must be between 1 byte and 5 MB.");
            byte[] bytes = file.getBytes();
            boolean png = bytes.length > 8 && (bytes[0] & 255) == 137 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71;
            boolean jpeg = bytes.length > 3 && (bytes[0] & 255) == 255 && (bytes[1] & 255) == 216 && (bytes[2] & 255) == 255;
            if ((!png && !jpeg) || ImageIO.read(new ByteArrayInputStream(bytes)) == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only valid JPEG or PNG image files are accepted.");
            String ext = png ? ".png" : ".jpg"; String name = UUID.randomUUID() + ext;
            Path dir = Path.of(uploadDir).toAbsolutePath().normalize(); Files.createDirectories(dir); Files.write(dir.resolve(name), bytes);
            String url = "/uploads/" + name; audit.record("UPLOAD", "image", name, "Event image upload"); return Map.of("imageUrl", url);
        } catch (ResponseStatusException e) { throw e; }
        catch (Exception e) { throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Image upload failed."); }
    }
}

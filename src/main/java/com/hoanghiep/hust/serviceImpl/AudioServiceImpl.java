package com.hoanghiep.hust.serviceImpl;

import com.hoanghiep.hust.exception.AudioException;
import com.hoanghiep.hust.service.IAudioService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
@NoArgsConstructor
public class AudioServiceImpl implements IAudioService {

    private final Path rootLocation = Path.of("src\\main\\resources\\static\\audios");

//    @Autowired
//    public AudioServiceImpl(String location) {
//        this.rootLocation = Paths.get(location);
//    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new AudioException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new AudioException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }

//            StringBuilder fileNames = new StringBuilder();
//            Path fileNameAndPath = Paths.get("src\\main\\resources\\static\\audios", file.getOriginalFilename());
//            fileNames.append(file.getOriginalFilename());
//            Files.write(fileNameAndPath, file.getBytes());


        }
        catch (IOException e) {
            throw new AudioException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new AudioException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new AudioException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new AudioException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

//    @Override
//    public void init() {
//        try {
//            Files.createDirectories(rootLocation);
//        }
//        catch (IOException e) {
//            throw new AudioException("Could not initialize storage", e);
//        }
//    }
}

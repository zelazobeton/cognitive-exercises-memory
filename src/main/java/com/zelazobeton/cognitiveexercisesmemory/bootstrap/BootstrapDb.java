package com.zelazobeton.cognitiveexercisesmemory.bootstrap;

import static com.zelazobeton.cognitiveexercisesmemory.constants.FileConstants.FORWARD_SLASH;
import static com.zelazobeton.cognitiveexercisesmemory.constants.FileConstants.IMG_FOLDER;
import static com.zelazobeton.cognitiveexercisesmemory.constants.FileConstants.MEMORY_IMG_FOLDER;
import static com.zelazobeton.cognitiveexercisesmemory.constants.FileConstants.MICROSERVICE_NAME;
import static com.zelazobeton.cognitiveexercisesmemory.constants.FileConstants.VERSION_1;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryImg;
import com.zelazobeton.cognitiveexercisesmemory.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercisesmemory.services.ResourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("bootstrap")
public class BootstrapDb implements CommandLineRunner {
    @Value("${frontend-address}")
    private String frontendAddress;
    private final MemoryImgRepository memoryImgRepository;
    private final ResourceService resourceService;

    @Override
    public void run(String... args) {
        this.loadMemoryImages();
    }

    private void loadMemoryImages() {
        Path memoryImagesFolder = this.resourceService.getPath(MEMORY_IMG_FOLDER);
        List<MemoryImg> memoryImgs = new ArrayList<>();
        for (File file : Objects.requireNonNull(memoryImagesFolder.toFile().listFiles())) {
            if (!file.isDirectory()) {
                String imgAddress = this.frontendAddress + MICROSERVICE_NAME + VERSION_1 + IMG_FOLDER + FORWARD_SLASH +
                        file.getName();
                memoryImgs.add(MemoryImg.builder().address(imgAddress).build());
            }
        }
        this.memoryImgRepository.saveAll(memoryImgs);
    }
}

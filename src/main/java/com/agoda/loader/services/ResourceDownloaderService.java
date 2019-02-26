package com.agoda.loader.services;

import com.agoda.loader.DownloadingResult;
import com.agoda.loader.LoaderType;
import com.agoda.loader.exception.WrongResourcesException;
import com.agoda.loader.imp.Loader;
import com.agoda.loader.imp.LoaderFactory;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.String.format;

@Slf4j
public class ResourceDownloaderService {

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    private final CompletionService<DownloadingResult> executor = new ExecutorCompletionService<>(pool);

    private final FileService fileService;

    public ResourceDownloaderService(FileService fileService) {
        this.fileService = fileService;
    }

    public void download(String folderLocation, String[] urls) {
        download(folderLocation, urls, true);
    }

    public void download(String folderLocation, String[] urls, boolean ignoreWrongResources) {
        Path folderPath = Paths.get(folderLocation);
        fileService.maybeCreateDirectory(folderPath);
        DownloadRequest downloadRequest = buildDownloadRequest(urls, ignoreWrongResources, folderPath);
        submitDownloads(downloadRequest);
        awaitDownloads(downloadRequest);
    }

    //doesn't finish
    private void awaitDownloads(DownloadRequest downloadRequest) {
        for (int i = 0; i < downloadRequest.getRecourseDetails().size(); i++) {
            try {
                DownloadingResult result = executor.take().get();
                log.info("Download finished for - {} : {}", result.getFilePath(), result.isSuccessful());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Something bad happened with one of downloading thread", e);
            }
        }
    }

    private void submitDownloads(DownloadRequest downloadRequest) {
        downloadRequest.getRecourseDetails().forEach( resourceDetails -> {
            Loader loader = LoaderFactory.getLoader(LoaderType.valueOf(resourceDetails.getUrl().getProtocol().toUpperCase()));
            executor.submit(() -> loader.load(resourceDetails.getUrl(), resourceDetails.getFilePath()));
        });
    }

    private DownloadRequest buildDownloadRequest(String[] resources, boolean ignoreWrongResources, Path folderPath) {
        List<ResourceDetails> resourceDetails = new ArrayList<>();
        List<String> unprocessedResources = new ArrayList<>();

        for (String resource: resources) {
            try {
                URL resourcesUrl = new URL(resource);
                Path filePath = folderPath.resolve(FilenameUtils.getName(resourcesUrl.getPath()));

                if (canProcess(filePath)) {
                    resourceDetails.add(ResourceDetails.builder()
                            .filePath(filePath)
                            .url(resourcesUrl)
                            .build());
                } else {
                    unprocessedResources.add(resource);
                }

            } catch (MalformedURLException e) {
                log.error(format("Wrong URL of the recourse %s", resource), e);
                unprocessedResources.add(resource);
            }
        }

        if (!ignoreWrongResources && !unprocessedResources.isEmpty()) {
            throw new WrongResourcesException(String.join(",", unprocessedResources));
        }

        return DownloadRequest.builder()
                .folderLocation(folderPath)
                .recourseDetails(resourceDetails)
                .build();
    }

    private boolean canProcess(Path filePath) {
        if (fileService.exists(filePath)) {
            if (!fileService.isWritable(filePath)) {
                log.error("Unable to write to the file - {}", filePath);
                return false;
            } else {
                log.info("File - {} will be overridden", filePath);
            }
        }
        return true;
    }

    @Value
    @Builder
    static class DownloadRequest {
        private Path folderLocation;

        private List<ResourceDetails> recourseDetails;
    }

    @Value
    @Builder
    static class ResourceDetails {
        private Path filePath;

        private URL url;
    }
}

package com.agoda.downloading.services;

import com.agoda.downloading.domain.DownloadingResult;
import com.agoda.downloading.downloaders.Downloader;
import com.agoda.downloading.downloaders.DownloaderFactory;
import com.agoda.downloading.exception.WrongResourcesException;
import com.agoda.filesoperations.services.IOService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.String.format;

@Slf4j
@AllArgsConstructor
public class ResourceDownloaderService implements AutoCloseable {

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    private final CompletionService<DownloadingResult> executor = new ExecutorCompletionService<>(pool);

    private final IOService IOService;

    private final DownloaderFactory downloaderFactory;

    public void download(String folderLocation, Collection<String> urls) {
        download(folderLocation, urls, true);
    }

    public void download(String folderLocation, Collection<String> urls, boolean ignoreWrongResources) {
        Path folderPath = Paths.get(folderLocation);
        boolean directoryCreated = IOService.maybeCreateDirectory(folderPath);

        if (!directoryCreated) {
            throw new WrongResourcesException(String.format("Unable to create directory %s", folderLocation));
        }

        DownloadRequest downloadRequest = buildDownloadRequest(urls, folderPath, ignoreWrongResources);
        submitDownloads(downloadRequest);
        awaitDownloads(downloadRequest);
    }

    private void submitDownloads(DownloadRequest downloadRequest) {
        downloadRequest.getRecourseDetails().forEach( resourceDetails -> {
            Downloader loader = downloaderFactory.getDownloader(resourceDetails.getUrl().getProtocol());
            executor.submit(() -> loader.download(resourceDetails.getUrl(), resourceDetails.getFilePath()));
            log.info("Submit downloading task for url - {}", resourceDetails.getUrl());
        });
    }

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

    private DownloadRequest buildDownloadRequest(Collection<String> resources, Path folderPath, boolean ignoreWrongResources) {
        List<ResourceDetails> resourceDetails = new ArrayList<>();
        List<String> unprocessedResources = new ArrayList<>();
        Set<Path> usedPaths = new HashSet<>();

        for (String resource: resources) {
            URL resourcesUrl = getUrl(resource);

            if (resourcesUrl == null) {
                unprocessedResources.add(resource);
                continue;
            }

            Path filePath = buildFilePath(folderPath, resourcesUrl);

            if (canProcess(filePath) && !usedPaths.contains(filePath)) {
                resourceDetails.add(ResourceDetails.builder()
                        .filePath(filePath)
                        .url(resourcesUrl)
                        .build());
                usedPaths.add(filePath);
            } else {
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

    private URL getUrl(String resource) {
        try {
           return new URL(resource);
        } catch (MalformedURLException e) {
            log.error(format("Wrong URL of the recourse %s", resource), e);
            return null;
        }
    }

    private Path buildFilePath(Path folderPath, URL resourcesUrl) {
        String fileName = constructFileNameFrom(resourcesUrl);
        return folderPath.resolve(fileName);
    }

    private String constructFileNameFrom(URL resourcesUrl) {
        String host = "";
        String file = "";
        String protocol = "";

        if (resourcesUrl.getProtocol() != null) {
            protocol= resourcesUrl.getProtocol();
        }
        if (resourcesUrl.getHost() != null) {
            host = resourcesUrl.getHost();
        }
        if (resourcesUrl.getFile() != null) {
            file = resourcesUrl.getFile();
        }
        return (protocol + host + file).replace("/", "_");
    }

    private boolean canProcess(Path filePath) {
        if (IOService.exists(filePath)) {
            if (!IOService.isWritable(filePath)) {
                log.error("Unable to write to the file - {}", filePath);
                return false;
            } else {
                log.info("File - {} will be overridden", filePath);
            }
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        pool.shutdown();
        pool.awaitTermination(1000L, TimeUnit.MILLISECONDS);
    }

    @Value
    @Builder
    private static class DownloadRequest {
        private Path folderLocation;

        private Collection<ResourceDetails> recourseDetails;
    }

    @Value
    @Builder
    private static class ResourceDetails {
        private Path filePath;

        private URL url;
    }
}

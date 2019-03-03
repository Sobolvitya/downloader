package com.agoda.downloading.services

import com.agoda.downloading.domain.DownloadingResult
import com.agoda.downloading.downloaders.Downloader
import com.agoda.downloading.downloaders.DownloaderFactory
import com.agoda.downloading.exception.WrongResourcesException
import com.agoda.filesoperations.services.IOService
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class ResourceDownloaderServiceTest extends Specification {

    IOService fileService = Mock(IOService)

    DownloaderFactory downloaderFactory = Mock(DownloaderFactory)

    def "exception is throw if directory couldn't be created"() {
	  given:
	  ResourceDownloaderService service = new ResourceDownloaderService(fileService, downloaderFactory)

	  def location = "location"
	  def resource = 'ftp://file.com/file'
	  when:
	  service.download(location, [resource])
	  then:
	  fileService.maybeCreateDirectory(Paths.get(location)) >> false
	  def ex = thrown(WrongResourcesException.class)
	  ex.message == 'Unable to create directory location'
    }

    def "should fail in bad url when wrong resources shouldnt be skipped"() {
	  given:
	  ResourceDownloaderService service = new ResourceDownloaderService(fileService, downloaderFactory)

	  def location = "location"
	  def resource1 = 'ftp://file.com/file'
	  def badUrl = 'not url'

	  when:
	  service.download(location, [resource1, badUrl], false)

	  then:
	  fileService.maybeCreateDirectory(Paths.get(location)) >> true
	  def ex = thrown(WrongResourcesException.class)
	  ex.message == 'not url'
    }

    def "should fail if fail is not writable"() {
	  given:
	  ResourceDownloaderService service = new ResourceDownloaderService(fileService, downloaderFactory)

	  def location = "location"
	  def resource1 = 'ftp://file.com/file'
	  def expectedPath1 = Paths.get('location/ftpfile.com_file')

	  when:
	  service.download(location, [resource1], false)

	  then:
	  fileService.maybeCreateDirectory(Paths.get(location)) >> true
	  fileService.exists(expectedPath1) >> true
	  fileService.isWritable(expectedPath1) >> false
	  def ex = thrown(WrongResourcesException.class)
	  ex.message == 'ftp://file.com/file'
    }

    def "should fail when url duplication and wrong resources shouldnt be skipped"() {
	  given:
	  ResourceDownloaderService service = new ResourceDownloaderService(fileService, downloaderFactory)

	  def location = "location"
	  def resource1 = 'ftp://file.com/file'
	  def resource2 = 'ftp://file.com/file'

	  when:
	  service.download(location, [resource1, resource2], false)

	  then:
	  fileService.maybeCreateDirectory(Paths.get(location)) >> true
	  def ex = thrown(WrongResourcesException.class)
	  ex.message == 'ftp://file.com/file'
    }

    def "should download resources"() {
	  given:
	  Downloader downloader1 = Mock(Downloader)
	  Downloader downloader2 = Mock(Downloader)

	  ResourceDownloaderService service = new ResourceDownloaderService(fileService, downloaderFactory)

	  def location = "location"
	  def resource1 = 'ftp://file.com/file'
	  def resource2 = 'http://file.com/file1'
	  def expectedPath1 = Paths.get('location/ftpfile.com_file')
	  def expectedPath2 = Paths.get('location/httpfile.com_file1')

	  when:
	  service.download(location, [resource1, resource2], false)

	  then:
	  fileService.maybeCreateDirectory(Paths.get(location)) >> true
	  downloaderFactory.getDownloader('ftp') >> downloader1
	  downloaderFactory.getDownloader('http') >> downloader2
	  downloader1.download(new URL(resource1), expectedPath1) >> buildResult(expectedPath1, true)
	  downloader2.download(new URL(resource2), expectedPath2) >> buildResult(expectedPath2, true)
    }

    def "should download resources even if one failed"() {
	  given:
	  Downloader downloader1 = Mock(Downloader)
	  Downloader downloader2 = Mock(Downloader)

	  ResourceDownloaderService service = new ResourceDownloaderService(fileService, downloaderFactory)

	  def location = "location"
	  def resource1 = 'ftp://file.com/file'
	  def resource2 = 'http://file.com/file1'
	  def expectedPath1 = Paths.get('location/ftpfile.com_file')
	  def expectedPath2 = Paths.get('location/httpfile.com_file1')

	  when:
	  service.download(location, [resource1, resource2], false)

	  then:
	  fileService.maybeCreateDirectory(Paths.get(location)) >> true
	  downloaderFactory.getDownloader('ftp') >> downloader1
	  downloaderFactory.getDownloader('http') >> downloader2
	  downloader1.download(new URL(resource1), expectedPath1) >> {throw new Exception("Something went wrong")}
	  downloader2.download(new URL(resource2), expectedPath2) >> buildResult(expectedPath2, true)
    }

    static DownloadingResult buildResult(Path path, boolean result) {
	  return DownloadingResult.builder()
		    .filePath(path)
		    .successful(result)
		    .build()
    }
}

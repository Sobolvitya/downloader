package com.agoda.downloading.downloaders

import com.agoda.filesoperations.services.IOService
import spock.lang.Specification

import java.nio.channels.ReadableByteChannel
import java.nio.file.Path
import java.nio.file.Paths

class DownloaderToFileTest extends Specification {

    IOService ioService = Mock(IOService)

    def "should download resource"() {
	  given:
	  Downloader downloader = new ChannelBasedDownloader(ioService)
	  ReadableByteChannel readableByteChannel = Mock(ReadableByteChannel)
	  URL from = new URL("http://test.com/test")
	  Path to = Paths.get("random")

	  when:
	  def result = downloader.download(from, to)

	  then:
	  ioService.openReadableChannel(from) >> readableByteChannel
	  1 * ioService.writeToFile(to, readableByteChannel)

	  result.filePath == to
	  result.successful
    }

    def "should fail and provide result in case of IO failures"() {
	  given:
	  Downloader downloader = new ChannelBasedDownloader(ioService)
	  ReadableByteChannel readableByteChannel = Mock(ReadableByteChannel)
	  URL from = new URL("http://test.com/test")
	  Path to = Paths.get("random")

	  when:
	  def result = downloader.download(from, to)

	  then:
	  ioService.openReadableChannel(from) >> {throw new IOException("something wrong, now I long for yesterday..")}
	  0 * ioService.writeToFile(to, readableByteChannel)

	  result.filePath == to
	  !result.successful
    }
}

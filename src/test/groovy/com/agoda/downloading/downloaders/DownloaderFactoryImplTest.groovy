package com.agoda.downloading.downloaders

import spock.lang.Specification
import spock.lang.Unroll

class DownloaderFactoryImplTest extends Specification {

    @Unroll
    def "should return correct downloader for #protocol"(String protocol, Class clazz) {
	  when:
	  def downloader = new DownloaderFactoryImpl().getDownloader(protocol)

	  then:
	  downloader.class == clazz

	  where:
	  protocol    | clazz
	  'something' | NoopDownloader.class
	  'http'      | ChannelBasedDownloader.class
	  'ftp'       | ChannelBasedDownloader.class
	  'sftp'      | ChannelBasedDownloader.class
    }
}

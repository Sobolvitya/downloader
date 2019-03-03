package com.agoda.application

import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ApplicationIntTest extends Specification {

    private static final GenericContainer fileServer =
		new GenericContainer("halverneus/static-file-server")
			  .withClasspathResourceMapping("server", "/web", BindMode.READ_WRITE)

    private Path folderPath

    static {
	  fileServer.start()
    }

    def setup() {
	  folderPath = Paths.get(getClass().getClassLoader().getResource("local").getPath())
    }

    def "check downloading files"() {
	  when:
	  String[] strings = ['-f', folderPath.toString(), '-r', "http://localhost:${fileServer.getMappedPort(8080)}/text.txt"]
	  new Application().run(strings)

	  then:
	  List<String> lines = Files.readAllLines(folderPath.resolve("httplocalhost_text.txt"))
	  lines.size() == 2
	  lines.get(0) == "Hello World!"
	  lines.get(1) == "Checking how does it work..."
    }

    def "shouldn't do any download if one of url is incorrect"() {
	  when:
	  String[] strings = ['-f', folderPath.toString(), '-r', "http://localhost:${fileServer.getMappedPort(8080)}/unsuccessful.txt,random-wrong-url", "-i", "false"]
	  new Application().run(strings)
	  then:
	  !Files.exists(folderPath.resolve("httplocalhost_unsuccessful.txt"))
    }

    def cleanup() {
	  def path1 = folderPath.resolve('httplocalhost_unsuccessful.txt')
	  if (Files.exists(path1)) {
		Files.delete(path1)
	  }
	  def path2 = folderPath.resolve('httplocalhost_text.txt')
	  if (Files.exists(path2)) {
		Files.delete(path2)
	  }
    }
}

package com.agoda.application.parsers

import com.agoda.application.domain.ApplicationArguments
import spock.lang.Specification
import spock.lang.Unroll

class ApplicationArgumentParserTest extends Specification {

    @Unroll
    def "#description"(String description, String[] args, ApplicationArguments parsedArgsExpected) {
	  given:
	  def parser = new ApplicationArgumentParser()

	  when:
	  ApplicationArguments parsedArgs = parser.parseCommandLineArguments(args)

	  then:
	  parsedArgsExpected == parsedArgs

	  where:
	  description                                     | args                                            | parsedArgsExpected
	  "construct object with all options"             | ['-f', 'path', '-r', 'res1,res2', '-i', 'true'] | new ApplicationArguments('path', ['res1', 'res2'], 'true')
	  "construct object without not required options" | ['-f', 'path', '-r', 'res1,res2']               | new ApplicationArguments('path', ['res1', 'res2'], null)
	  "return null if required option is not present" | ['-f', 'path']                                  | null
    }
}

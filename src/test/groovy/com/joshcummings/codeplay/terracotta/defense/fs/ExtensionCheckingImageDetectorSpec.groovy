package com.joshcummings.codeplay.terracotta.defense.fs

import spock.lang.Specification
import spock.lang.Unroll

class ExtensionCheckingImageDetectorSpec extends Specification {

    ExtensionCheckingImageDetector checkingImageDetector

    def setup() {
        checkingImageDetector = new ExtensionCheckingImageDetector()
    }

    @Unroll
    def "#image is an image"() {
        when:
            def isAnImage = checkingImageDetector.isAnImage(image, null)

        then:
            isAnImage

        where:
            image << ["test.png", "test.jpg", "test.gif"]
    }

    @Unroll
    def "#image is not an image"() {
        when:
            def isAnImage = checkingImageDetector.isAnImage(image, null)

        then:
            !isAnImage

        where:
            image << ["test.doc", "test.pdf"]
    }

}

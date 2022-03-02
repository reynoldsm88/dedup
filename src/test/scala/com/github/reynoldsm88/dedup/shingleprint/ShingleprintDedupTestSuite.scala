package com.github.reynoldsm88.dedup.shingleprint

import better.files.File
import com.github.reynoldsm88.dedup.{Dedup, TestBase}

class ShingleprintDedupTestSuite extends TestBase {

    private val DOCS : File = File( "src/test/resources/docs" )

    "Shingleprint Deduplication" should "return nothing if the document is not a duplicate" in {
        val dedup : Dedup = init()
        val results = dedup.check( "This is just a bunch of random words. They do not appear anywhere else in the corpus so this document should not match any duplicates." )

        results.size shouldBe 0
    }

    "Shingleprint Deduplication" should "identify a duplicate document (content added to the beginning)" in {
        val dedup : Dedup = init()
        val duplicate : String =
            s"""
               |Republished from Associated Press, editorialized by some crazy Russian propaganda bot. All rights reserved.
               |
               |${( DOCS / "nytimes-nasa-moon.txt" ).contentAsString}
               |""".stripMargin

        val results = dedup.check( duplicate )

        results.size shouldBe 1
        results.head.docId shouldBe "nytimes-nasa-moon"
    }

    "Shingleprint Deduplication" should "identify a duplicate document (content added to the end)" in {
        val dedup : Dedup = init()
        val duplicate : String =
            s"""
               |${( DOCS / "skies-allmusic.txt" ).contentAsString}
               |
               |Republished from Associated Press, editorialized by some crazy Russian propaganda bot. All rights reserved.
               |""".stripMargin

        val results = dedup.check( duplicate )

        results.size shouldBe 1
        results.head.docId shouldBe "skies-allmusic"
    }

    "Shingleprint Deduplication" should "update the cache with a new document" in {
        val dedup : Dedup = init()

        val docId = "test_doc"
        val text =
            """A brand-new space telescope will soon reveal a hidden vision of the cosmos, potentially transforming our understanding of black holes, supernovas and even the nature of the universe itself.
              |
              |No, not that one.
              |
              |Much attention is being devoted this month to the James Webb Space Telescope, from NASA and the European Space Agency, which is set to launch on Dec. 22. But a more exclusive cadre of astronomers watched excitedly on Thursday during the trip to space of a smaller, but also transformative, observatory.
              |
              |NASA launched the Imaging X-ray Polarimetry Explorer, or IXPE mission, on a SpaceX Falcon 9 rocket from Kennedy Space Center in Florida at 1 a.m. Eastern. The spacecraft cost a mere $188 million, compared with the James Webb’s mammoth budget of $9.7 billion, and is expected to demonstrate a new form of astronomy. It will, for the first time, perform imaging X-ray polarimetry in orbit, a technique that could offer astronomers insights that no other telescope can match.
              |
              |“It’s giving us information about some of the most bizarre and exciting objects in space,” said Thomas Zurbuchen, the associate administrator of NASA’s science mission directorate.
              |
              |IXPE (pronounced by the mission team as “ix-pee”) was placed into an orbit 340 miles above Earth after its launch. The telescope will spend several weeks there deploying its scientific instruments and testing its equipment, then begin its two-year mission.
              |
              |X-rays are a useful way to observe the universe. Emitted from extremely energetic objects, they allow astronomers to probe events — superheated jets near black holes or explosions of stars, for example — in a way other wavelengths, such as visible light, cannot. But X-rays can be studied only from space because they are mostly absorbed by Earth’s atmosphere.
              |
              |A variety of dedicated X-ray space telescopes and instruments have launched to orbit, most notably NASA’s Chandra X-ray and ESA’s XMM-Newton observatories, which both launched in 1999. With spacecraft like these, scientists have unveiled the birthplaces of stars inside gaseous nebulas and mapped the spread of dark matter in clusters of galaxies, among other pioneering work.""".stripMargin


        val checkNotPresent = dedup.check( text )
        checkNotPresent.size shouldBe 0

        dedup.update( docId, text )

        val checkUpdate = dedup.check( text )
        checkUpdate.size shouldBe 1
        checkUpdate.head.docId shouldBe docId
    }

    "Shingleprint Deduplication" should "handle documents with significant overlap" in {
        val dedup : Dedup = init()

        val duplicate =
            s"""Black Pus is Lightning Bolt drummer Brian Chippendale’s pop-inspired side project. Don’t get the wrong idea, though. The drummer’s solo-musings are only “pop” in comparison to his main gig, whose expansive, abstract thrashing sounds sort of like a flaming 8-bit meteor plunging into the Burning Man festival.
               |Those same drum-and-bass-meets-Jackson Pollock rhythms provide the foundations for Black Pus, but here, Chippendale sometimes reigns in the pounding to make way for singsongy melodies. Over the number of small-run CD-R and CD releases that he’s done under the name since 2005, there’s been a marked progression from free-form havok to music that is, in its own claustrophobic and LSD-singed way, kind of tuneful. And this latest release, even more so
               |
               |${( DOCS / "nytimes-nasa-moon.txt" ).contentAsString}
               |""".stripMargin


        val results = dedup.check( duplicate )

        results.size shouldBe 1
        results.head.docId shouldBe "nytimes-nasa-moon"
    }

    "Shingleprint Deduplication" should "ignore documents with null text" in {
        val dedup : Dedup = init()
        val results = dedup.check( null )
        results.isEmpty shouldBe true
    }

    "Shingleprint Deduplication" should "ignore documents with empty text" in {
        val dedup : Dedup = init()
        val results = dedup.check( "" )
        results.isEmpty shouldBe true
    }

    private def init( ) : Dedup = {
        val dedup = new ShingleprintDedup( maxWords = 100, threshold = 0.9 )
        DOCS.list.foreach( f => dedup.update( f.nameWithoutExtension, f.contentAsString ) )
        dedup
    }
}

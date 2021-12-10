package com.github.reynoldsm88.dedup.shingleprint

import com.github.reynoldsm88.dedup.TestBase

class MemoryShingleprintCacheTestSuite extends TestBase {

    "In-memory shingleprint cache" should "add some shingleprints" in {
        val cache = new MemoryShingleprintCache

        cache.update( "doc1", Set( 1, 2, 3, 4 ) )
        cache.update( "doc2", Set( 5, 6, 7, 8 ) )

        cache.size() shouldBe 8
    }

    "In-memory shingleprint cache" should "duplicate shingleprints" in {
        val cache = new MemoryShingleprintCache

        cache.update( "doc1", Set( 1, 2, 3, 4 ) )
        cache.update( "doc2", Set( 1, 3, 5, 7 ) )

        cache.size() shouldBe 6
    }

    "In-memory shingleprint cache" should "find documents with overlapping shingleprints" in {
        val cache = new MemoryShingleprintCache

        cache.update( "doc1", Set( 1, 2, 3, 4 ) )
        cache.update( "doc2", Set( 1, 3, 5, 7 ) )

        val results = cache.search( Set( 3, 8, 9, 10 ) )

        results.size shouldBe 2
        results shouldBe Set( "doc1", "doc2" )
    }
}

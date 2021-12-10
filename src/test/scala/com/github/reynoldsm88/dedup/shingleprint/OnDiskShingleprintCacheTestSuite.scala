package com.github.reynoldsm88.dedup.shingleprint

import better.files.File
import com.github.reynoldsm88.dedup.TestBase

import scala.util.Random

class OnDiskShingleprintCacheTestSuite extends TestBase {

    // concurrent tests cause weird problems with deleting the chronicle map cache directory, hence we need to use unique folders and
    // manually clean it up between tests
    val target : File = File( "./target" )

    "ChronicleMap disk cache" should "add some shingleprints" in {
        val (shingleFile, mappingsFile) = createTestCacheFiles( Random.nextInt( 100000 ) )
        val cache = new OnDiskShingleprintCache( shingleFile, mappingsFile )

        cache.update( "doc1", Set( 1, 2, 3, 4 ) )
        cache.update( "doc2", Set( 5, 6, 7, 8 ) )

        cache.size() shouldBe 8

        cache.close()
        deleteTestCacheFiles( shingleFile, mappingsFile )
    }

    "ChronicleMap disk cache" should "not duplicate shingleprints" in {
        val (shingleFile, mappingsFile) = createTestCacheFiles( Random.nextInt( 100000 ) )
        val cache = new OnDiskShingleprintCache( shingleFile, mappingsFile )

        cache.update( "doc1", Set( 1, 2, 3, 4 ) )
        cache.update( "doc2", Set( 1, 3, 5, 7 ) )

        cache.size() shouldBe 6

        cache.close()
        deleteTestCacheFiles( shingleFile, mappingsFile )
    }

    "ChronicleMap disk cache" should "find documents with overlapping shingleprints" in {
        val (shingleFile, mappingsFile) = createTestCacheFiles( Random.nextInt( 100000 ) )
        val cache = new OnDiskShingleprintCache( shingleFile, mappingsFile )

        cache.update( "doc1", Set( 1, 2, 3, 4 ) )
        cache.update( "doc2", Set( 1, 3, 5, 7 ) )

        val results = cache.search( Set( 3, 8, 9, 10 ) )

        results.size shouldBe 2
        results shouldBe Set( "doc1", "doc2" )

        cache.close()
        deleteTestCacheFiles( shingleFile, mappingsFile )
    }

    "ChronicleMap disk cache" should "allow concurrent access to the cache" in {
        val (shingleFile, mappingsFile) = createTestCacheFiles( Random.nextInt( 100000 ) )
        val cacheOne = new OnDiskShingleprintCache( shingleFile, mappingsFile )

        cacheOne.update( "doc1", Set( 1, 2, 3, 4 ) )
        cacheOne.update( "doc2", Set( 1, 3, 5, 7 ) )

        val initialResults = cacheOne.search( Set( 3, 8, 9, 10 ) )

        initialResults.size shouldBe 2
        initialResults shouldBe Set( "doc1", "doc2" )

        // another process starts reading the cache
        val cacheTwo = new OnDiskShingleprintCache( shingleFile, mappingsFile )
        cacheTwo.update( "doc3", Set( 4, 11, 12, 13 ) )

        val resultsOne = cacheOne.search( Set( 4 ) )
        val resultsTwo = cacheTwo.search( Set( 4 ) )

        resultsOne shouldBe resultsTwo
        resultsTwo shouldBe Set( "doc1", "doc3" )

        cacheOne.close()
        cacheTwo.close()
        deleteTestCacheFiles( shingleFile, mappingsFile )
    }

    private def createTestCacheFiles( slug : Int ) : (File, File) = {
        val shingleFile : File = ( target / s"shingles_${slug}.cache" )
        val mappingsFile : File = ( target / s"shingle_doc_mappings_${slug}.cache" )
        (shingleFile, mappingsFile)
    }

    private def deleteTestCacheFiles( files : File* ) : Unit = {
        files.foreach( _.delete( swallowIOExceptions = true ) )
    }

}

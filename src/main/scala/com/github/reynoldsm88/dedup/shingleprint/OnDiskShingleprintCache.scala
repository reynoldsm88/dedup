package com.github.reynoldsm88.dedup.shingleprint

import better.files.File
import com.github.reynoldsm88.dedup.Cache
import net.openhft.chronicle.map.ChronicleMap
import net.openhft.chronicle.set.ChronicleSet

import java.lang.{Integer => JInt}
import java.util.{Set => JSet}
import scala.collection.JavaConverters._

object OnDiskShingleprintCache {
    val SHINGLES_FILENAME : String = "shingles.cache"
    val SHINGLES_DOC_MAPPINGS_FILENAME : String = "shingle_doc_mappings.cache"


    def apply( dataDir : File ) : OnDiskShingleprintCache = {
        new OnDiskShingleprintCache( ( dataDir / SHINGLES_FILENAME ), ( dataDir / SHINGLES_DOC_MAPPINGS_FILENAME ) )
    }
}

class OnDiskShingleprintCache( val shingleCacheFile : File, val mappingsCacheFile : File ) extends Cache {

    private lazy val shingleDocMappings : ChronicleMap[ JInt, JSet[ String ] ] = {
        ChronicleMap.of[ JInt, JSet[ String ] ]( classOf[ JInt ], classOf[ JSet[ String ] ] )
          .name( "shingle_doc_mappings" )
          .averageValue( Set( "0ac46cdc9b7baa8c7236aff1481f27e9", "0ac46cdc9b7baa8c7236aff1481f0000" ).asJava )
          .entries( 50000 )
          .createPersistedTo( mappingsCacheFile.toJava )
    }

    private lazy val shingles : ChronicleSet[ JInt ] = {
        ChronicleSet
          .of[ JInt ]( classOf[ JInt ] )
          .name( "shingles" )
          .entries( 1000000 )
          .createPersistedTo( shingleCacheFile.toJava )
    }

    def search( fingerprints : Set[ Int ] ) : Set[ String ] = {
        val matches = fingerprints.filter( shingles.contains )
        matches.flatMap( m => shingleDocMappings.get( m ).asScala )
    }

    def update( docId : String, fingerprints : Set[ Int ] ) : Unit = {
        fingerprints.foreach( f => {
            shingles.add( f )
            Option( shingleDocMappings.get( f ) ) match {
                case Some( existing : JSet[ String ] ) => {
                    val updated = ( existing.asScala + docId ).asJava
                    shingleDocMappings.put( f, updated )
                }
                case None => shingleDocMappings.put( f, Set( docId ).asJava )
            }
        } )
    }

    override def size( ) : Int = shingles.size()

    override def close( ) : Unit = {
        shingleDocMappings.close()
        shingles.close()
    }


}

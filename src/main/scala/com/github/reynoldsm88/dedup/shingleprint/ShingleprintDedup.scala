package com.github.reynoldsm88.dedup.shingleprint

import com.github.reynoldsm88.dedup.{Cache, Dedup, Duplicate}

import java.util.Objects
import scala.util.hashing.MurmurHash3

/**
 * This algorithm is credited to Dustin Boswell in his blog post 'Real-Time Document DeDuplication'.
 * The original article can be found here: https://medium.com/@dustinboswell/real-time-document-deduplication-d5fb5982812
 */
class ShingleprintDedup( val maxWords : Int,
                         val threshold : Double,
                         window : Int = 10,
                         cache : Cache = new MemoryShingleprintCache ) extends Dedup {

    override def check( text : String ) : Set[ Duplicate ] = {
        val docShingles = shingleprints( text )
        if ( docShingles.nonEmpty ) {
            val matches = cache.search( docShingles )
            if ( matches.nonEmpty ) matches.map( m => Duplicate( m, 1.0 ) )
            else Set()
        } else Set()
    }

    override def update( id : String, text : String ) : Unit = {
        val shingles = shingleprints( text )
        if ( shingles.nonEmpty ) {
            cache.update( id, shingles )
        }
    }

    private def shingleprints( text : String ) : Set[ Int ] = {
        if ( text != null && text.nonEmpty ) {
            val shingleOne = text.substring( 0, text.length / 2 )
            val shingleTwo = text.substring( text.length / 2, text.length )

            val (min1, max2) = minMaxHash( shingleOne )
            val (min2, max1) = minMaxHash( shingleTwo )

            Set( hashCombine( min1, min2 ), hashCombine( min1, max2 ), hashCombine( max1, min2 ), hashCombine( max1, max2 ) )
        } else Set()
    }

    private def minMaxHash( text : String ) : (Int, Int) = {
        val hashes : Seq[ Int ] = text.sliding( window ).map( MurmurHash3.stringHash ).toSeq
        (hashes.min, hashes.max)
    }

    private def hashCombine( x : Int, y : Int ) : Int = {
        Objects.hash( x.asInstanceOf[ AnyRef ], y.asInstanceOf[ AnyRef ] )
    }

}

package com.github.reynoldsm88.dedup.shingleprint

import com.github.reynoldsm88.dedup.Cache

import scala.collection.mutable

class MemoryShingleprintCache extends Cache {

    private val shingleDocMappings : mutable.Map[ Int, Set[ String ] ] = mutable.Map()

    private val shingleCache : mutable.Set[ Int ] = mutable.Set()

    override def search( fingerprints : Set[ Int ] ) : Set[ String ] = {
        val overlap = shingleCache.intersect( fingerprints )
        if ( overlap.nonEmpty ) overlap.flatMap( shingleDocMappings( _ ) ).toSet
        else Set()
    }

    override def update( docId : String, fingerprints : Set[ Int ] ) : Unit = {
        fingerprints.foreach( f => {
            shingleCache.add( f )
            if ( !shingleDocMappings.contains( f ) ) shingleDocMappings.put( f, Set( docId ) )
            else shingleDocMappings.put( f, shingleDocMappings( f ) + docId )
        } )
    }

    override def size( ) : Int = shingleCache.size

    override def close( ) : Unit = {} // no-op

}

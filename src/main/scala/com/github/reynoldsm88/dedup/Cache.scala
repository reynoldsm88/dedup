package com.github.reynoldsm88.dedup

trait Cache {

    def search( fingerprints : Set[ Int ] ) : Set[ String ]

    def update( docId : String, fingerprints : Set[ Int ] )

    def size( ) : Int

    def close( ) : Unit

}

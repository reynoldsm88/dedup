package com.github.reynoldsm88.dedup

trait Dedup {

    val maxWords : Int

    val threshold : Double

    def check( text : String ) : Set[ Duplicate ]

    def update( id : String, text : String ) : Unit

}

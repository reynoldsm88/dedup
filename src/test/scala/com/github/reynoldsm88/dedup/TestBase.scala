package com.github.reynoldsm88.dedup

import org.scalatest.{BeforeAndAfterEach, FlatSpecLike, Matchers}

trait TestBase extends FlatSpecLike with Matchers with BeforeAndAfterEach {
    // the chronicle map people think its okay to send data to google analytics without explicit consent...
    // https://github.com/OpenHFT/Chronicle-Map/blob/ea/DISCLAIMER.adoc
    System.setProperty( "chronicle.announcer.disable", "true" )
}
